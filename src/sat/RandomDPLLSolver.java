package sat;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Optional;

public class RandomDPLLSolver extends DPLLSolver {

    @Override
    protected Optional<Integer> choosePropositionToAssign(CNFProblem problem, CNFSolution solution) {
        return Sets.difference(
                        new HashSet<>(problem.getPropositions()),
                        solution.getAssignment().keySet()
                )
                .stream()
                .findAny();
    }
}
