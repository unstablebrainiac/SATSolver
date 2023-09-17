import sat.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PuzzleSolverMain {

    private static final String RESOURCES_DIRECTORY = "resources";
    public static final String PUZZLE_FILE = "puzzle.cnf";

    private static CNFProblem readProblemFromFile(String filename) throws IOException {
        CNFProblem problem = new CNFProblem();
        // TODO Split by '0' rather than '\n' to support any DIMACS file
        try (var lines = Files.lines(Path.of(RESOURCES_DIRECTORY, filename))) {
            lines.filter(line -> !line.isBlank())
                    .filter(line -> !line.startsWith("c"))
                    .filter(line -> !line.startsWith("p"))
                    .map(line -> line.split(" "))
                    .map(Arrays::stream)
                    .map(literals -> literals.map(Integer::parseInt))
                    .map(literals -> literals.filter(literal -> literal != 0))
                    .map(literals -> literals.collect(Collectors.toList()))
                    .map(Clause::new)
                    .forEach(problem::addClause);
        }
        return problem;
    }

    public static void main(String[] args) throws IOException {
        long startTime = System.nanoTime();
        CNFProblem problem = readProblemFromFile(PUZZLE_FILE);
        long endTime = System.nanoTime();
        System.out.println("Time to read problem: " + (endTime - startTime) / 1_000_000 + " ms");

        long seed = Long.parseLong(args[0]);
        List<CNFSolver> solvers = Arrays.asList(
                new RandomDPLLSolver(seed),
                new TwoClauseDPLLSolver(seed)
        );
        List<CNFSolution> solutions = new ArrayList<>();
        for (CNFSolver solver : solvers) {
            CNFProblem problemCopy = new CNFProblem(problem);
            System.out.println("Solving with " + solver.getClass().getSimpleName());
            startTime = System.nanoTime();
            CNFSolution solution = solver.solve(problemCopy);
            endTime = System.nanoTime();
            System.out.println(solution);
            solutions.add(solution);
            System.out.println("Time to solve: " + (endTime - startTime) / 1_000_000 + " ms\n");
        }

        if (solutions.stream().map(CNFSolution::isSatisfiable).distinct().count() == 1) {
            System.out.println("All solutions are the same");
        } else {
            System.out.println("Solutions are different");
        }
    }
}

// 3 7 14 16 25 28 31 37 45 49 54 56 65 68 72 78 84 87 95 96 101 110 114 117 123 130 131 138 142 149 153 156 164 170 172 180 184 188 192 196 202 210 211 219 223 226 235 239 242 248 254 257 263 266 275 280 284 286 293 297 304 306 315 318 322 330 333 337 341 349 353 359 361 370 372 -375