package sat.solver;

import sat.CNFProblem;
import sat.CNFSolution;
import sat.Clause;

import java.util.*;
import java.util.concurrent.TimeoutException;

public abstract class DPLLSolver implements CNFSolver {

    @Override
    public CNFSolution solve(CNFProblem problem, long timeoutInMs) throws TimeoutException {
        long startTime = System.currentTimeMillis();
        long endTime = timeoutInMs == 0 ? 0 : startTime + timeoutInMs;
        CNFSolution solution = new CNFSolution(new HashMap<>(), true);
        boolean satisfiable = dpll(problem, solution, endTime);
        solution.setSatisfiable(satisfiable);
        return solution;
    }

    private boolean dpll(CNFProblem problem, CNFSolution solution, long endTime) throws TimeoutException {
        // Check if we have timed out
        if (endTime != 0 && System.currentTimeMillis() > endTime) {
            throw new TimeoutException();
        }
        // Keep applying unit preference rule until there are no more unit clauses
        while (applyUnitPreferenceRule(problem, solution)) {
            if (!solution.isSatisfiable()) {
                return false;
            }
            if (problem.getClauses().isEmpty()) {
                return true;
            }
        }

        Optional<Integer> maybeProposition = choosePropositionToAssign(problem, solution);
        if (maybeProposition.isEmpty()) {
            return problem.getClauses().isEmpty();
        }
        solution.incrementNumDPLLSteps();
        Integer proposition = maybeProposition.get();

        boolean firstAssignment = chooseFirstAssignment(proposition, problem, solution);

        CNFProblem problemCopy = new CNFProblem(problem);
        CNFSolution solutionCopy = new CNFSolution(solution);

        if (applyAssignment(proposition, firstAssignment, problemCopy, solutionCopy)) {
            if (dpll(problemCopy, solutionCopy, endTime)) {
                solution.setAssignment(solutionCopy.getAssignment());
                solution.setNumDPLLSteps(solutionCopy.getNumDPLLSteps());
                return true;
            }
        }

        problemCopy = new CNFProblem(problem);
        solutionCopy = new CNFSolution(solution);

        if (applyAssignment(proposition, !firstAssignment, problemCopy, solutionCopy)) {
            if (dpll(problemCopy, solutionCopy, endTime)) {
                solution.setAssignment(solutionCopy.getAssignment());
                return true;
            }
        }

        return false;
    }

    protected abstract Optional<Integer> choosePropositionToAssign(CNFProblem problem, CNFSolution solution);

    protected abstract boolean chooseFirstAssignment(Integer proposition, CNFProblem problem, CNFSolution solution);

    private boolean applyUnitPreferenceRule(CNFProblem problem, CNFSolution solution) {
        Optional<Integer> maybeUnitProposition = problem.getClauses()
                .stream()
                .filter(clause -> clause.size() == 1)
                .findFirst()
                .map(clause -> clause.getProposition(0));
        maybeUnitProposition
                .ifPresent(proposition -> applyAssignment(Math.abs(proposition), proposition > 0, problem, solution));
        return maybeUnitProposition.isPresent();
    }

    private boolean applyAssignment(Integer proposition, boolean isPositive, CNFProblem problem, CNFSolution solution) {
        if (proposition < 0) {
            throw new IllegalArgumentException("Proposition must be positive");
        }
        if (!solution.checkAndSetAssignment(proposition, isPositive)) {
            solution.setSatisfiable(false);
            return false;
        }
        List<Clause> positiveClauses = Optional.ofNullable(problem.getClausesContainingProposition(proposition * (isPositive ? 1 : -1))).orElse(new ArrayList<>());
        List<Clause> negativeClauses = Optional.ofNullable(problem.getClausesContainingProposition(proposition * (isPositive ? -1 : 1))).orElse(new ArrayList<>());

        problem.getClauses().removeAll(positiveClauses);
        negativeClauses.forEach(clause -> clause.getPropositions().remove(Integer.valueOf(proposition * (isPositive ? -1 : 1))));
        if (problem.getClauses().stream().anyMatch(Clause::isEmpty)) {
            solution.setSatisfiable(false);
            return false;
        }
        return true;
    }
}
