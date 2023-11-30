package ltlf.main;

import ltlf.formula.RandomLTLfGenerator;
import ltlf.graph.*;
import ltlf.solver.ReachabilitySolver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExperimentationMain {
    private static final String DATA_FILE_NAME = "data.csv";
    private static final String RESOURCES_DIRECTORY = "resources/ltlf";
    private static final String DATASET_DIRECTORY = RESOURCES_DIRECTORY + "/dataset";

    public static void main(String[] args) throws IOException {
        runExperimentsOnRandomGraphs(false);
        runExperimentsOnRandomGraphs(true);
        runExperimentsOnRandomLTLfFormulae();
        runExperimentsOnLTLfDatasets();
    }

    private static void runExperimentsOnRandomGraphs(boolean randomizeFinalStates) throws FileNotFoundException {
        for (int size = 10; size <= 1000; size *= 10) {
            for (int i = 0; i < 100; i++) {
                System.out.println("Generating random graph..." + i + "/" + 100 + " (size: " + size + ")");
                RandomGraphGenerator generator = new RandomGraphGenerator(i, size);
                Graph randomGraph = generator.generate();

                for (int finalStateCount = 0; finalStateCount < size; finalStateCount += Math.max(size / 100, 1)) {

                    BFSGraphIterator bfsGraphIterator = new BFSGraphIterator(randomGraph);
                    RandomGraphIterator randomGraphIterator = new RandomGraphIterator(randomGraph, i, 1000);

                    Set<Node> finalStates = new HashSet<>();
                    int finalStateCountCopy = finalStateCount;
                    Comparator<Node> incrementalComparator = Comparator.comparing(node -> new Random(Long.parseLong(node.getName())).nextInt());
                    Comparator<Node> randomizedComparator = Comparator.comparing(node -> new Random(finalStateCountCopy * 1000L + Long.parseLong(node.getName())).nextInt());
                    randomGraph.getNodes()
                            .stream()
                            .sorted(randomizeFinalStates ? randomizedComparator : incrementalComparator)
                            .limit(finalStateCount)
                            .forEach(finalStates::add);

                    runExperiment("RandomGraph", finalStates, bfsGraphIterator, randomGraphIterator, size, i, randomizeFinalStates);
                }
            }
        }
    }

    private static void runExperimentsOnRandomLTLfFormulae() throws FileNotFoundException {
        int size = 30;
        for (int propositions = 10; propositions <= size; propositions += 10) {
            for (int i = 0; i < 100; i++) {
                System.out.println("Generating random LTLf formula..." + i + "/" + 100 + " (size: " + size + ", propositions: " + propositions + ")");
                RandomLTLfGenerator generator = new RandomLTLfGenerator(i, size, propositions);
                String formula = generator.generate();
                System.out.println("Formula: " + formula);
                System.out.println("Generating graph..." + i + "/" + 100);
                LTLfGraphGenerator graphGenerator = new LTLfGraphGenerator(formula);
                Graph graph = graphGenerator.generate();
                if (!graphGenerator.getFinalStates().isEmpty()) {
                    System.out.println("Final states: " + graphGenerator.getFinalStates());
                }
                BFSGraphIterator bfsGraphIterator = new BFSGraphIterator(graph);
                RandomGraphIterator randomGraphIterator = new RandomGraphIterator(graph, i, 1000);

                runExperiment("RandomLTLf", graphGenerator.getFinalStates(), bfsGraphIterator, randomGraphIterator, propositions, i, false);
            }
        }
    }

    private static void runExperimentsOnLTLfDatasets() throws IOException {
        List<File> files = Files.walk(Paths.get(DATASET_DIRECTORY))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".ltlf"))
                .sorted()
                .map(Path::toFile)
                .toList();

        for (File file : files) {
            System.out.println("Processing file: " + file.getName() + " (" + (files.indexOf(file) + 1) + "/" + files.size() + ")");
            String formula = Files.readString(file.toPath());

            // There is a difference in syntax between the dataset and what MONA expects.
            formula = formula.replaceAll("X", "WX");
            formula = formula.replaceAll("WX\\[!]", "X");

            System.out.println("Formula: " + formula);
            System.out.println("Generating graph...");
            LTLfGraphGenerator graphGenerator = new LTLfGraphGenerator(formula);
            Graph graph = graphGenerator.generate();
            if (graph == null) {
                continue;
            }
            if (!graphGenerator.getFinalStates().isEmpty()) {
                System.out.println("Final states: " + graphGenerator.getFinalStates());
            }
            BFSGraphIterator bfsGraphIterator = new BFSGraphIterator(graph);
            RandomGraphIterator randomGraphIterator = new RandomGraphIterator(graph, 0, 1000);

            String graphName = file.toPath().toString().substring(DATASET_DIRECTORY.length() + 1, file.toPath().toString().length() - ".ltlf".length());
            runExperiment("LTLfDataset/" + graphName, graphGenerator.getFinalStates(), bfsGraphIterator, randomGraphIterator, 0, 0, false);
        }
    }

    private static void runExperiment(String experimentType, Set<Node> finalStates, BFSGraphIterator bfsGraphIterator, RandomGraphIterator randomGraphIterator, int size, int i, boolean randomizeFinalStates) throws FileNotFoundException {
        ReachabilitySolver solver = new ReachabilitySolver();

        System.out.println("Solving with BFS...");
        long startTime = System.nanoTime();
        boolean bfsResult = solver.isReachable(bfsGraphIterator, finalStates);
        long endTime = System.nanoTime();
        long bfsTime = endTime - startTime;
        System.out.println("Solving with Random...");
        startTime = System.nanoTime();
        boolean randomResult = solver.isReachable(randomGraphIterator, finalStates);
        endTime = System.nanoTime();
        long randomTime = endTime - startTime;

        if (!bfsResult && randomResult) {
            throw new IllegalStateException("BFS says it's not reachable, but random says it is!");
        }

        if (!randomResult) {
            randomTime += bfsTime;
        }

        writeResultsToFile(experimentType, size, finalStates.size(), i, bfsResult, bfsTime, randomTime, !randomResult, randomizeFinalStates);
    }

    private static void writeResultsToFile(String experimentType, int size, int finalStates, int instance, boolean bfsResult, long bfsTime, long randomTime, boolean fallback, boolean randomizeFinalStates) throws FileNotFoundException {
        File csvOutputFile = new File(RESOURCES_DIRECTORY + "/" + DATA_FILE_NAME);
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(csvOutputFile, true))) {
            pw.println(Stream.of(
                            experimentType,
                            size,
                            finalStates,
                            instance,
                            bfsResult,
                            bfsTime,
                            randomTime,
                            fallback,
                            randomizeFinalStates
                    )
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
        }
    }
}
