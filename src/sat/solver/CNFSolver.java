package sat.solver;

import sat.CNFProblem;
import sat.CNFSolution;

public interface CNFSolver {
    CNFSolution solve(CNFProblem problem);
}