package ltlf.graph;

import java.util.Random;

public class RandomGraphGenerator extends GraphGenerator {
    private final Random random;
    private final int size;

    public RandomGraphGenerator(long seed, int size) {
        this.random = new Random(seed);
        this.size = size;
    }

    public Graph generate() {
        Graph graph = new Graph();
        for (int i = 0; i < size; i++) {
            graph.addNode(new Node(String.valueOf(i)));
        }
        for (Node node : graph.getNodes()) {
            for (Node other : graph.getNodes()) {
                if (random.nextBoolean()) {
                    graph.addEdge(node, other);
                }
            }
        }
        return graph;
    }
}
