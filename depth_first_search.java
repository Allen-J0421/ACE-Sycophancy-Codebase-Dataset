import java.util.*;

class Edge<N> {
    private final N from;
    private final N to;
    private final double weight;

    Edge(N from, N to) { this(from, to, 1.0); }

    Edge(N from, N to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public N getFrom() { return from; }
    public N getTo()   { return to; }
    public double getWeight() { return weight; }

    @Override
    public String toString() { return from + " -[" + weight + "]-> " + to; }
}

interface GraphInterface<N> {
    Iterable<N> nodes();
    List<Edge<N>> getEdges(N node);
    int size();
}

class Graph<N> implements GraphInterface<N> {
    private final Map<N, List<Edge<N>>> adj = new LinkedHashMap<>();

    public void addEdge(N u, N v) { addEdge(u, v, 1.0); }

    public void addEdge(N u, N v, double weight) {
        adj.computeIfAbsent(u, k -> new ArrayList<>()).add(new Edge<>(u, v, weight));
        adj.computeIfAbsent(v, k -> new ArrayList<>()).add(new Edge<>(v, u, weight));
    }

    @Override
    public Iterable<N> nodes() { return adj.keySet(); }

    @Override
    public List<Edge<N>> getEdges(N node) {
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
        for (Edge<N> edge : graph.getEdges(node)) {
            N neighbor = edge.getTo();
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
                    for (Edge<N> edge : graph.getEdges(v)) {
                        N neighbor = edge.getTo();
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
        // Unweighted integer graph — same topology as before
        Graph<Integer> intGraph = new Graph<>();
        intGraph.addEdge(1, 2);
        intGraph.addEdge(0, 3);
        intGraph.addEdge(2, 0);
        intGraph.addEdge(5, 4);

        GraphTraverser<Integer> traverser = new GraphTraverser<>(new DfsStrategy<>());
        List<Integer> dfsOrder = new ArrayList<>();
        traverser.traverse(intGraph, dfsOrder::add);
        System.out.println("DFS collect:  " + dfsOrder);

        int[] count = {0};
        traverser.setStrategy(new BfsStrategy<>());
        traverser.traverse(intGraph, node -> count[0]++);
        System.out.println("BFS count:    " + count[0] + " nodes");

        // Weighted city graph — demonstrates edge metadata
        Graph<String> cityGraph = new Graph<>();
        cityGraph.addEdge("A", "B", 5.0);
        cityGraph.addEdge("A", "C", 2.0);
        cityGraph.addEdge("B", "D", 3.0);

        System.out.println("Edges from A: " + cityGraph.getEdges("A"));

        GraphTraverser<String> cityTraverser = new GraphTraverser<>(new DfsStrategy<>());
        System.out.print("DFS cities:   ");
        cityTraverser.traverse(cityGraph, node -> System.out.print(node + " "));
        System.out.println();

        cityTraverser.setStrategy(new BfsStrategy<>());
        System.out.print("BFS cities:   ");
        cityTraverser.traverse(cityGraph, node -> System.out.print(node + " "));
        System.out.println();
    }
}
