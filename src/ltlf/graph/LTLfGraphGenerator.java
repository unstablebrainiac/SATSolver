package ltlf.graph;

import ltlf.ltlf2dfa.PythonInvocationService;

import java.util.*;

public class LTLfGraphGenerator extends GraphGenerator {
    private final String formula;
    private final Set<Node> finalStates;

    public LTLfGraphGenerator(String formula) {
        this.formula = formula;
        this.finalStates = new HashSet<>();
    }

    @Override
    public Graph generate() {
        return parse(PythonInvocationService.runPythonScriptInVenv("src/ltlf/ltlf2dfa/ltlf_to_dfa.py", formula));
    }

    public Set<Node> getFinalStates() {
        return finalStates;
    }

    private Graph parse(String dotString) {
        if (dotString.startsWith("Traceback")) {
            System.out.println(dotString);
            System.out.println("Timed out while generating graph.");
            return null;
        }
        System.out.println(dotString);
        if (dotString.contains(" node [shape = doublecircle]; 0.0;")) {
            return null;
        }
        Map<Integer, Node> nodes = new HashMap<>();
        Set<Integer> finalStateIndices = new HashSet<>();
        String[] lines = dotString.split("\n");
        Graph graph = new Graph();
        for (String line : lines) {
            if (line.startsWith(" node [shape = doublecircle];")
                    && !line.equals(" node [shape = doublecircle];")) {
                Arrays.stream(
                                line.substring(" node [shape = doublecircle];".length())
                                        .split(";")
                        )
                        .map(String::trim)
                        .forEach(s -> finalStateIndices.add(Integer.parseInt(s)));
            } else if (line.contains("->") && !line.startsWith(" init")) {
                String[] parts = line.split("->");
                int from = Integer.parseInt(parts[0].trim());
                int to = Integer.parseInt(parts[1].split("\\[")[0].trim());
                Node fromNode = nodes.computeIfAbsent(from, name -> {
                    Node node = new Node(String.valueOf(name));
                    graph.addNode(node);
                    return node;
                });
                Node toNode = nodes.computeIfAbsent(to, name -> {
                    Node node = new Node(String.valueOf(name));
                    graph.addNode(node);
                    return node;
                });
                graph.addEdge(fromNode, toNode);
            }
        }
        finalStateIndices
                .stream()
                .map(nodes::get)
                .forEach(finalStates::add);
        return graph;
    }
}
