package ltlf.graph;

import java.util.List;

public class BFSGraphIterator extends GraphIterator {

    private final List<Node> bfsSequence;
    private int currentIndex = 0;

    public BFSGraphIterator(Graph graph) {
        this.bfsSequence = graph.getBFSSequence();
    }

    @Override
    public boolean hasNext() {
        return currentIndex < bfsSequence.size();
    }

    @Override
    public Node next() {
        return bfsSequence.get(currentIndex++);
    }
}
