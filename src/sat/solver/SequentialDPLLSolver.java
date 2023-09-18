package sat.solver;

import sat.CNFProblem;
import sat.CNFSolution;

import java.util.Optional;

public class SequentialDPLLSolver extends DPLLSolver {

    @Override
    protected Optional<Integer> choosePropositionToAssign(CNFProblem problem, CNFSolution solution) {
        return problem.getPropositions()
                .stream()
                .filter(proposition -> !solution.getAssignment().containsKey(proposition))
                .findFirst();
    }
}
