package sat;

import java.util.HashMap;

public interface CNFSolver {
    CNFSolution solve(CNFProblem problem);
}