package main;

import sat.CNFProblem;
import sat.CNFSolution;
import sat.RandomProblemGenerator;
import sat.solver.*;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExperimentationMain {

    private static final String DATA_FILE_NAME = "data.csv";

    private static final String RESOURCES_DIRECTORY = "resources";

    public static void main(String[] args) throws IOException {
        int N = 150;
        int K = 3;
        for (float LbyN = 3; LbyN <= 6; LbyN += 0.2f) {
            int L = Math.round(LbyN * N);
            System.out.println("Starting experiments for N = " + N + ", L = " + L);
            for (int i = 1; i <= 100; i++) {
                long seed = (long) i * L;
                System.out.println("Starting experiment set " + i);
                RandomProblemGenerator randomProblemGenerator = new RandomProblemGenerator(seed);

                long startTime = System.nanoTime();
                CNFProblem problem = randomProblemGenerator.generate(N, L, K);
                long endTime = System.nanoTime();
                System.out.println("Time to generate: " + (endTime - startTime) / 1_000_000 + " ms");

                List<CNFSolver> solvers = List.of(
                        new RandomDPLLSolver(seed),
                        new TwoClauseDPLLSolver(seed),
                        new SequentialDPLLSolver(),
                        new SequentialTwoClauseDPLLSolver()
                );

                for (CNFSolver solver : solvers) {
                    CNFProblem problemCopy = new CNFProblem(problem);
                    startTime = System.nanoTime();
                    CNFSolution solution = solver.solve(problemCopy);
                    endTime = System.nanoTime();
                    System.out.println("Time to solve with " + solver.getClass().getSimpleName() + ": " + (endTime - startTime) / 1_000_000 + " ms");
                    writeResultsToFile(LbyN, N, L, seed, problem, solver.getClass().getSimpleName(), solution, (endTime - startTime) / 1_000_000);
                }
            }
        }
    }

    private static void writeResultsToFile(float LbyN, int N, int L, long seed, CNFProblem problem, String solver, CNFSolution solution, long time) throws FileNotFoundException {
        File csvOutputFile = new File(RESOURCES_DIRECTORY + "/" + DATA_FILE_NAME);
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(csvOutputFile, true))) {
            pw.println(Stream.of(
                    LbyN,
                    N,
                    L,
                    seed,
                    problem,
                    problem.getPropositions().size(),
                    solver,
                    solution.isSatisfiable(),
                    solution,
                    solution.getNumDPLLSteps(),
                    time
            )
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
        }
    }
}
