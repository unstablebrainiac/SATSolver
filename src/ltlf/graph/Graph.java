package ltlf.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Graph {
    private final List<Node> nodes;
    private final Map<Node, List<Node>> next;
    private Node root;

    public Graph(List<Node> nodes, Map<Node, List<Node>> next, Node root) {
        this.nodes = nodes;
        this.next = next;
        this.root = root;
    }

    public Graph() {
        this.nodes = new ArrayList<>();
        this.next = new java.util.HashMap<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
        if (root == null) {
            root = node;
        }
    }

    public void addEdge(Node from, Node to) {
        next.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
    }

    public int size() {
        return nodes.size();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Node> getNext(Node node) {
        return next.getOrDefault(node, new ArrayList<>());
    }

    public Node getRoot() {
        return root;
    }

    public List<Node> getBFSSequence() {
        List<Node> bfsSequence = new ArrayList<>();
        bfsSequence.add(root);
        List<Node> currentLevel = new ArrayList<>();
        currentLevel.add(root);
        while (!currentLevel.isEmpty()) {
            List<Node> nextLevel = new ArrayList<>();
            for (Node node : currentLevel) {
                List<Node> nextNodes = next.get(node);
                if (nextNodes != null) {
                    for (Node nextNode : nextNodes) {
                        if (!bfsSequence.contains(nextNode)) {
                            bfsSequence.add(nextNode);
                            nextLevel.add(nextNode);
                        }
                    }
                }
            }
            currentLevel = nextLevel;
        }
        return bfsSequence;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "nodes=" + nodes +
                ", next=" + next +
                ", root=" + root +
                '}';
    }
}
