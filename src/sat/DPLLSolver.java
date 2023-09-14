package sat;

import java.util.*;

public abstract class DPLLSolver implements CNFSolver {

    @Override
    public CNFSolution solve(CNFProblem problem) {
        CNFSolution solution = new CNFSolution(new HashMap<>(), true);
        boolean satisfiable = dpll(problem, solution);
        solution.setSatisfiable(satisfiable);
        return solution;
    }

    private boolean dpll(CNFProblem problem, CNFSolution solution) {
        // Keep applying unit preference rule until there are no more unit clauses
        while (applyUnitPreferenceRule(problem, solution)) {
            if (problem.getClauses().isEmpty()) {
                return true;
            }
            if (!solution.isSatisfiable()) {
                return false;
            }
        }

        Optional<Integer> maybeProposition = choosePropositionToAssign(problem, solution);
        if (maybeProposition.isEmpty()) {
            return true;
        }
        Integer proposition = maybeProposition.get();

        CNFProblem problemCopy = new CNFProblem(problem);
        CNFSolution solutionCopy = new CNFSolution(solution);

//        System.out.println("Trying positive assignment for proposition " + proposition);
        if (applyAssignment(proposition, true, problemCopy, solutionCopy)) {
            if (dpll(problemCopy, solutionCopy)) {
                solution.setAssignment(solutionCopy.getAssignment());
                return true;
            }
        }

        problemCopy = new CNFProblem(problem);
        solutionCopy = new CNFSolution(solution);

//        System.out.println("Trying negative assignment for proposition " + proposition);
        if (applyAssignment(proposition, false, problemCopy, solutionCopy)) {
            if (dpll(problemCopy, solutionCopy)) {
                solution.setAssignment(solutionCopy.getAssignment());
                return true;
            }
        }

        return false;
    }

    protected abstract Optional<Integer> choosePropositionToAssign(CNFProblem problem, CNFSolution solution);

    private boolean applyUnitPreferenceRule(CNFProblem problem, CNFSolution solution) {
        List<Integer> unitPropositions = problem.getClauses()
                .stream()
                .filter(clause -> clause.size() == 1)
                .map(clause -> clause.getProposition(0))
                .toList();
        unitPropositions
                .forEach(proposition -> applyAssignment(Math.abs(proposition), proposition > 0, problem, solution));
        return !unitPropositions.isEmpty();
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
        problem.getClauses().removeIf(Clause::isEmpty);
        return true;
    }
}
