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
        System.out.println("Time to read problem: " + (endTime - startTime) / 1_000_000 + "ms");

        long seed = Long.parseLong(args[0]);
        List<CNFSolver> solvers = Arrays.asList(new RandomDPLLSolver(seed), new TwoClauseDPLLSolver(seed));
        List<CNFSolution> solutions = new ArrayList<>();
        for (CNFSolver solver : solvers) {
            CNFProblem problemCopy = new CNFProblem(problem);
            System.out.println("Solving with " + solver.getClass().getSimpleName());
            startTime = System.nanoTime();
            CNFSolution solution = solver.solve(problemCopy);
            endTime = System.nanoTime();
            System.out.println(solution);
            solutions.add(solution);
            System.out.println("Time to solve: " + (endTime - startTime) / 1_000_000 + "ms");
        }

        if (solutions.stream().map(CNFSolution::isSatisfiable).distinct().count() == 1) {
            System.out.println("All solutions are the same");
        } else {
            System.out.println("Solutions are different");
        }
    }
}
