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

class GraphBuilder<N> {
    private final Map<N, List<Edge<N>>> adj = new LinkedHashMap<>();

    /** Adds a bidirectional edge: u→v and v→u. */
    public GraphBuilder<N> edge(N u, N v) { return edge(u, v, 1.0); }

    public GraphBuilder<N> edge(N u, N v, double weight) {
        adj.computeIfAbsent(u, k -> new ArrayList<>()).add(new Edge<>(u, v, weight));
        adj.computeIfAbsent(v, k -> new ArrayList<>()).add(new Edge<>(v, u, weight));
        return this;
    }

    /** Adds a unidirectional edge: u→v only. */
    public GraphBuilder<N> directedEdge(N u, N v) { return directedEdge(u, v, 1.0); }

    public GraphBuilder<N> directedEdge(N u, N v, double weight) {
        adj.computeIfAbsent(u, k -> new ArrayList<>()).add(new Edge<>(u, v, weight));
        adj.computeIfAbsent(v, k -> new ArrayList<>());  // register sink so it appears in nodes()
        return this;
    }

    public Graph<N> build() {
        // Defensive copy so the built graph is unaffected by subsequent builder calls
        Map<N, List<Edge<N>>> snapshot = new LinkedHashMap<>();
        for (Map.Entry<N, List<Edge<N>>> entry : adj.entrySet()) {
            snapshot.put(entry.getKey(),
                Collections.unmodifiableList(new ArrayList<>(entry.getValue())));
        }
        return new Graph<>(Collections.unmodifiableMap(snapshot));
    }
}

class Graph<N> implements GraphInterface<N> {
    private final Map<N, List<Edge<N>>> adj;

    Graph(Map<N, List<Edge<N>>> adj) {
        this.adj = adj;
    }

    public static <N> GraphBuilder<N> builder() { return new GraphBuilder<>(); }

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

class TraversalContext<N> {
    private final GraphInterface<N> graph;
    private final NodeVisitor<N> visitor;

    TraversalContext(GraphInterface<N> graph, NodeVisitor<N> visitor) {
        this.graph = graph;
        this.visitor = visitor;
    }

    public Iterable<N> nodes()            { return graph.nodes(); }
    public List<Edge<N>> getEdges(N node) { return graph.getEdges(node); }
    public void visit(N node)             { visitor.visit(node); }
}

class TraversalState<N> {
    private final Set<N> visited = new HashSet<>();

    /** Marks node visited. Returns true if it was not previously visited. */
    public boolean markVisited(N node) { return visited.add(node); }

    public boolean isVisited(N node) { return visited.contains(node); }
}

interface TraversalStrategy<N> {
    void traverse(TraversalContext<N> context);
}

class DfsStrategy<N> implements TraversalStrategy<N> {
    @Override
    public void traverse(TraversalContext<N> context) {
        TraversalState<N> state = new TraversalState<>();
        for (N node : context.nodes()) {
            if (!state.isVisited(node)) visit(context, node, state);
        }
    }

    private void visit(TraversalContext<N> context, N node, TraversalState<N> state) {
        state.markVisited(node);
        context.visit(node);
        for (Edge<N> edge : context.getEdges(node)) {
            N neighbor = edge.getTo();
            if (!state.isVisited(neighbor)) visit(context, neighbor, state);
        }
    }
}

class BfsStrategy<N> implements TraversalStrategy<N> {
    @Override
    public void traverse(TraversalContext<N> context) {
        TraversalState<N> state = new TraversalState<>();
        Deque<N> queue = new ArrayDeque<>();

        for (N node : context.nodes()) {
            if (state.markVisited(node)) {
                queue.add(node);
                while (!queue.isEmpty()) {
                    N v = queue.poll();
                    context.visit(v);
                    for (Edge<N> edge : context.getEdges(v)) {
                        N neighbor = edge.getTo();
                        if (state.markVisited(neighbor)) queue.add(neighbor);
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

    public void traverse(TraversalContext<N> context) {
        strategy.traverse(context);
    }
}

public class DepthFirstSearch {
    public static void main(String[] args) {
        Graph<Integer> intGraph = Graph.<Integer>builder()
            .edge(1, 2)
            .edge(0, 3)
            .edge(2, 0)
            .edge(5, 4)
            .build();

        GraphTraverser<Integer> traverser = new GraphTraverser<>(new DfsStrategy<>());
        List<Integer> dfsOrder = new ArrayList<>();
        traverser.traverse(new TraversalContext<>(intGraph, dfsOrder::add));
        System.out.println("DFS collect:  " + dfsOrder);

        int[] count = {0};
        traverser.setStrategy(new BfsStrategy<>());
        traverser.traverse(new TraversalContext<>(intGraph, node -> count[0]++));
        System.out.println("BFS count:    " + count[0] + " nodes");

        Graph<String> cityGraph = Graph.<String>builder()
            .edge("A", "B", 5.0)
            .edge("A", "C", 2.0)
            .edge("B", "D", 3.0)
            .build();

        System.out.println("Edges from A: " + cityGraph.getEdges("A"));

        GraphTraverser<String> cityTraverser = new GraphTraverser<>(new DfsStrategy<>());
        System.out.print("DFS cities:   ");
        cityTraverser.traverse(new TraversalContext<>(cityGraph, node -> System.out.print(node + " ")));
        System.out.println();

        cityTraverser.setStrategy(new BfsStrategy<>());
        System.out.print("BFS cities:   ");
        cityTraverser.traverse(new TraversalContext<>(cityGraph, node -> System.out.print(node + " ")));
        System.out.println();

        // Directed graph — A→B→D, A→C (DAG; C and D are sinks)
        Graph<String> dag = Graph.<String>builder()
            .directedEdge("A", "B")
            .directedEdge("B", "D")
            .directedEdge("A", "C")
            .build();

        System.out.println("Edges from A: " + dag.getEdges("A"));
        System.out.println("Edges from D: " + dag.getEdges("D")); // sink — empty

        GraphTraverser<String> dagTraverser = new GraphTraverser<>(new DfsStrategy<>());
        System.out.print("DFS dag:      ");
        dagTraverser.traverse(new TraversalContext<>(dag, node -> System.out.print(node + " ")));
        System.out.println();

        dagTraverser.setStrategy(new BfsStrategy<>());
        System.out.print("BFS dag:      ");
        dagTraverser.traverse(new TraversalContext<>(dag, node -> System.out.print(node + " ")));
        System.out.println();
    }
}
