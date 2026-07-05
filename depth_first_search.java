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
    public Iterable<N> nodes() {
        return adj.keySet();
    }

    @Override
    public List<N> getNeighbors(N node) {
        return adj.getOrDefault(node, Collections.emptyList());
    }

    @Override
    public int size() {
        return adj.size();
    }
}

class TraversalResult<N> {
    private final String algorithm;
    private final List<N> visitOrder;

    TraversalResult(String algorithm, List<N> visitOrder) {
        this.algorithm = algorithm;
        this.visitOrder = Collections.unmodifiableList(new ArrayList<>(visitOrder));
    }

    public String getAlgorithm() { return algorithm; }
    public List<N> getVisitOrder() { return visitOrder; }
    public int size() { return visitOrder.size(); }

    @Override
    public String toString() {
        return algorithm + ": " + visitOrder;
    }
}

interface TraversalStrategy<N> {
    TraversalResult<N> traverse(GraphInterface<N> graph);
}

class DfsStrategy<N> implements TraversalStrategy<N> {
    private Set<N> visited;
    private final List<N> order = new ArrayList<>();

    @Override
    public TraversalResult<N> traverse(GraphInterface<N> graph) {
        visited = new HashSet<>();
        order.clear();
        for (N node : graph.nodes()) {
            if (!visited.contains(node)) visit(graph, node);
        }
        return new TraversalResult<>("DFS", order);
    }

    private void visit(GraphInterface<N> graph, N node) {
        visited.add(node);
        order.add(node);
        for (N neighbor : graph.getNeighbors(node)) {
            if (!visited.contains(neighbor)) visit(graph, neighbor);
        }
    }
}

class BfsStrategy<N> implements TraversalStrategy<N> {
    @Override
    public TraversalResult<N> traverse(GraphInterface<N> graph) {
        Set<N> visited = new HashSet<>();
        List<N> order = new ArrayList<>();
        Deque<N> queue = new ArrayDeque<>();

        for (N node : graph.nodes()) {
            if (!visited.contains(node)) {
                visited.add(node);
                queue.add(node);
                while (!queue.isEmpty()) {
                    N v = queue.poll();
                    order.add(v);
                    for (N neighbor : graph.getNeighbors(v)) {
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }
        return new TraversalResult<>("BFS", order);
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

    public TraversalResult<N> traverse(GraphInterface<N> graph) {
        return strategy.traverse(graph);
    }
}

public class DepthFirstSearch {
    public static void main(String[] args) {
        // Integer graph — same topology as before
        Graph<Integer> intGraph = new Graph<>();
        intGraph.addEdge(1, 2);
        intGraph.addEdge(0, 3);
        intGraph.addEdge(2, 0);
        intGraph.addEdge(5, 4);

        GraphTraverser<Integer> intTraverser = new GraphTraverser<>(new DfsStrategy<>());
        System.out.println(intTraverser.traverse(intGraph));
        intTraverser.setStrategy(new BfsStrategy<>());
        System.out.println(intTraverser.traverse(intGraph));

        // String graph — demonstrates non-integer node types
        Graph<String> strGraph = new Graph<>();
        strGraph.addEdge("A", "B");
        strGraph.addEdge("A", "C");
        strGraph.addEdge("B", "D");

        GraphTraverser<String> strTraverser = new GraphTraverser<>(new DfsStrategy<>());
        System.out.println(strTraverser.traverse(strGraph));
        strTraverser.setStrategy(new BfsStrategy<>());
        System.out.println(strTraverser.traverse(strGraph));
    }
}
