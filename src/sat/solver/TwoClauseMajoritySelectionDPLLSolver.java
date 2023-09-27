package sat.solver;

import sat.CNFProblem;
import sat.CNFSolution;

public class TwoClauseMajoritySelectionDPLLSolver extends TwoClauseDPLLSolver {
    public TwoClauseMajoritySelectionDPLLSolver(long seed) {
        super(seed);
    }

    @Override
    protected boolean chooseFirstAssignment(Integer proposition, CNFProblem problem, CNFSolution solution) {
        return problem.getClausesContainingProposition(proposition).size() >= problem.getClausesContainingProposition(-proposition).size();
    }
}
