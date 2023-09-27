package sat.solver;

import sat.CNFProblem;
import sat.CNFSolution;

import java.util.concurrent.TimeoutException;

public interface CNFSolver {
    /**
     * Solves the given CNF problem and returns a solution.
     * If the problem is satisfiable, the solution will contain a valid assignment.
     * If the problem is unsatisfiable, the solution will contain an empty assignment.
     * If the solver times out, the solution is non-deterministic.
     *
     * @param problem     the CNF problem to solve
     * @param timeoutInMs the timeout in milliseconds, or 0 for no timeout
     * @return the solution to the problem
     * @throws TimeoutException if the solver times out
     */
    CNFSolution solve(CNFProblem problem, long timeoutInMs) throws TimeoutException;
}