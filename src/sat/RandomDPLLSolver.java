package sat;

import java.util.Optional;
import java.util.Random;

public class RandomDPLLSolver extends DPLLSolver {

    Random random;

    public RandomDPLLSolver(long seed) {
        random = new Random(seed);
    }

    @Override
    protected Optional<Integer> choosePropositionToAssign(CNFProblem problem, CNFSolution solution) {
        int unassignedPropositionsCount = problem.getPropositions().size() - solution.getAssignment().size();
        int index = random.nextInt(unassignedPropositionsCount);
        return problem.getPropositions()
                .stream()
                .filter(proposition -> !solution.getAssignment().containsKey(proposition))
                .skip(index)
                .findFirst();
    }
}
