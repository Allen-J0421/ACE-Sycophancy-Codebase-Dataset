import java.util.*;

interface GraphInterface<N> {
    Iterable<N> nodes();
    List<N> getNeighbors(N node);
    int size();
}

class Graph<N> implements GraphInterface<N> {
    private final Map<N, List<N>> adj = new LinkedHashMap<>();

    public void addEdge(N u, N v) {
        adj.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
        adj.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
    }

    @Override
    public Iterable<N> nodes() { return adj.keySet(); }

    @Override
    public List<N> getNeighbors(N node) {
        return adj.getOrDefault(node, Collections.emptyList());
    }

    @Override
    public int size() { return adj.size(); }
}

@FunctionalInterface
interface NodeVisitor<N> {
    void visit(N node);
}

interface TraversalStrategy<N> {
    void traverse(GraphInterface<N> graph, NodeVisitor<N> visitor);
}

class DfsStrategy<N> implements TraversalStrategy<N> {
    @Override
    public void traverse(GraphInterface<N> graph, NodeVisitor<N> visitor) {
        Set<N> visited = new HashSet<>();
        for (N node : graph.nodes()) {
            if (!visited.contains(node)) visit(graph, node, visited, visitor);
        }
    }

    private void visit(GraphInterface<N> graph, N node, Set<N> visited, NodeVisitor<N> visitor) {
        visited.add(node);
        visitor.visit(node);
        for (N neighbor : graph.getNeighbors(node)) {
            if (!visited.contains(neighbor)) visit(graph, neighbor, visited, visitor);
        }
    }
}

class BfsStrategy<N> implements TraversalStrategy<N> {
    @Override
    public void traverse(GraphInterface<N> graph, NodeVisitor<N> visitor) {
        Set<N> visited = new HashSet<>();
        Deque<N> queue = new ArrayDeque<>();

        for (N node : graph.nodes()) {
            if (!visited.contains(node)) {
                visited.add(node);
                queue.add(node);
                while (!queue.isEmpty()) {
                    N v = queue.poll();
                    visitor.visit(v);
                    for (N neighbor : graph.getNeighbors(v)) {
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }
    }
}

class GraphTraverser<N> {
    private TraversalStrategy<N> strategy;

    GraphTraverser(TraversalStrategy<N> strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(TraversalStrategy<N> strategy) {
        this.strategy = strategy;
    }

    public void traverse(GraphInterface<N> graph, NodeVisitor<N> visitor) {
        strategy.traverse(graph, visitor);
    }
}

public class DepthFirstSearch {
    public static void main(String[] args) {
        Graph<Integer> intGraph = new Graph<>();
        intGraph.addEdge(1, 2);
        intGraph.addEdge(0, 3);
        intGraph.addEdge(2, 0);
        intGraph.addEdge(5, 4);

        GraphTraverser<Integer> traverser = new GraphTraverser<>(new DfsStrategy<>());

        // Collect visit order into a list
        List<Integer> dfsOrder = new ArrayList<>();
        traverser.traverse(intGraph, dfsOrder::add);
        System.out.println("DFS collect:  " + dfsOrder);

        // Count visited nodes without accumulating a list
        int[] count = {0};
        traverser.setStrategy(new BfsStrategy<>());
        traverser.traverse(intGraph, node -> count[0]++);
        System.out.println("BFS count:    " + count[0] + " nodes");

        // String graph — print each node as it is visited
        Graph<String> strGraph = new Graph<>();
        strGraph.addEdge("A", "B");
        strGraph.addEdge("A", "C");
        strGraph.addEdge("B", "D");

        GraphTraverser<String> strTraverser = new GraphTraverser<>(new DfsStrategy<>());
        System.out.print("DFS print:    ");
        strTraverser.traverse(strGraph, node -> System.out.print(node + " "));
        System.out.println();

        strTraverser.setStrategy(new BfsStrategy<>());
        System.out.print("BFS print:    ");
        strTraverser.traverse(strGraph, node -> System.out.print(node + " "));
        System.out.println();
    }
}
