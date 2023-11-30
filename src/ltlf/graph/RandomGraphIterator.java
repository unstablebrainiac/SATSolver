package ltlf.graph;

import java.util.List;
import java.util.Random;

public class RandomGraphIterator extends GraphIterator {

    private final Graph graph;
    private final Random random;
    private final int maxIterations;
    private int currentIteration = 0;
    private Node currentNode;

    public RandomGraphIterator(Graph graph, long seed, int maxIterations) {
        this.graph = graph;
        this.random = new Random(seed);
        this.maxIterations = maxIterations;
        this.currentNode = graph.getRoot();
    }

    @Override
    public boolean hasNext() {
        return currentIteration < maxIterations;
    }

    @Override
    public Node next() {
        currentIteration++;
        List<Node> candidates = graph.getNext(currentNode);
        if (candidates.isEmpty()) {
            currentNode = graph.getRoot();
            return currentNode;
        }
        currentNode = candidates.get(random.nextInt(candidates.size()));
        return currentNode;
    }
}
