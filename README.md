# SAT Solver

The primary aim of this project is to design and implement a SAT solver, which is a software tool used to solve Boolean satisfiability problems.

## Description

Initially, the project focuses on handling CNF inputs exclusively. It begins by parsing CNF files in the DIMACS format (as described in https://www.cs.rice.edu/~vardi/comp409/satformat.pdf). The objective is to provide a solution, presenting an assignment when the problem is satisfiable and reporting "UNSAT" when it is not.

## Usage

Run the sat.main class `PuzzleSolverMain` to solve the sample puzzle defined in the file `resources/puzzle.cnf`, using several different implementations of the DPLL Algorithm, and to assert the equality of their results.
