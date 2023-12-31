package sat.solver;

import sat.CNFProblem;
import sat.CNFSolution;

import java.util.Optional;

public class SequentialTwoClauseDPLLSolver extends DPLLSolver {

    @Override
    protected Optional<Integer> choosePropositionToAssign(CNFProblem problem, CNFSolution solution) {
        return problem.getClauses()
                .stream()
                .filter(clause -> clause.size() == 2)
                .findFirst()
                .map(clause -> clause.getProposition(0))
                .map(Math::abs)
                .or(() -> problem.getPropositions()
                        .stream()
                        .filter(proposition -> !solution.getAssignment().containsKey(proposition))
                        .findFirst()
                );
    }

    @Override
    protected boolean chooseFirstAssignment(Integer proposition, CNFProblem problem, CNFSolution solution) {
        return true;
    }
}
