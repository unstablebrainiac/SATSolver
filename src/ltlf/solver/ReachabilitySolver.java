package ltlf.solver;

import ltlf.graph.GraphIterator;
import ltlf.graph.Node;

import java.util.Set;

public class ReachabilitySolver {
    public boolean isReachable(GraphIterator graph, Set<Node> finalStates) {
        while (graph.hasNext()) {
            Node node = graph.next();
            if (finalStates.contains(node)) {
                return true;
            }
        }
        return false;
    }
}
