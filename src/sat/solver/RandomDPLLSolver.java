package sat.solver;

import sat.CNFProblem;
import sat.CNFSolution;

import java.util.Optional;
import java.util.Random;

public class RandomDPLLSolver extends DPLLSolver {

    private final Random random;

    public RandomDPLLSolver(long seed) {
        random = new Random(seed);
    }

    @Override
    protected Optional<Integer> choosePropositionToAssign(CNFProblem problem, CNFSolution solution) {
        int unassignedPropositionsCount = problem.getPropositions().size() - solution.getAssignment().size();
        if (unassignedPropositionsCount == 0) {
            return Optional.empty();
        }
        int index = random.nextInt(unassignedPropositionsCount);
        return problem.getPropositions()
                .stream()
                .filter(proposition -> !solution.getAssignment().containsKey(proposition))
                .skip(index)
                .findFirst();
    }

    @Override
    protected boolean chooseFirstAssignment(Integer proposition, CNFProblem problem, CNFSolution solution) {
        return random.nextBoolean();
    }
}
