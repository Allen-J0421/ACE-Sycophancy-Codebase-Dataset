import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

class Graph {
    private final int vertices;
    private final int[][] adj;
    private final int[] degree;

    Graph(int vertices) {
        this.vertices = vertices;
        adj = new int[vertices][];
        degree = new int[vertices];
        for (int i = 0; i < vertices; i++) {
            adj[i] = new int[4];
        }
    }

    void addEdge(int u, int v) {
        if (degree[u] == adj[u].length) {
            adj[u] = Arrays.copyOf(adj[u], adj[u].length * 2);
        }
        adj[u][degree[u]++] = v;
    }

    void forEachNeighbor(int u, IntConsumer action) {
        for (int i = 0; i < degree[u]; i++) {
            action.accept(adj[u][i]);
        }
    }

    int vertexCount() {
        return vertices;
    }

    Graph reverse() {
        Graph rev = new Graph(vertices);
        for (int u = 0; u < vertices; u++) {
            for (int i = 0; i < degree[u]; i++) {
                rev.addEdge(adj[u][i], u);
            }
        }
        return rev;
    }
}

class GraphFactory {
    static Graph fromEdges(int[][] edges, int vertexCount) {
        Graph graph = new Graph(vertexCount);
        for (int[] edge : edges) {
            graph.addEdge(edge[0], edge[1]);
        }
        return graph;
    }
}

class TraversalState {
    private final boolean[] visited;
    private final Stack<Integer> finishOrder;

    TraversalState(int vertexCount) {
        visited = new boolean[vertexCount];
        finishOrder = new Stack<>();
    }

    boolean isVisited(int u)   { return visited[u]; }
    void markVisited(int u)    { visited[u] = true; }
    void recordFinished(int u) { finishOrder.push(u); }
    boolean hasPending()       { return !finishOrder.isEmpty(); }
    int popNext()              { return finishOrder.pop(); }
}

class GraphTraversal {
    static void dfs(Graph graph, int start, TraversalState state,
                    IntConsumer onEnter, IntConsumer onExit) {
        state.markVisited(start);
        onEnter.accept(start);
        graph.forEachNeighbor(start, v -> {
            if (!state.isVisited(v)) dfs(graph, v, state, onEnter, onExit);
        });
        onExit.accept(start);
    }
}

interface SCCAlgorithm {
    List<List<Integer>> findSCCs(Graph graph);
}

class KosarajuSCC implements SCCAlgorithm {

    @Override
    public List<List<Integer>> findSCCs(Graph graph) {
        int V = graph.vertexCount();
        TraversalState pass1 = new TraversalState(V);

        for (int i = 0; i < V; i++) {
            if (!pass1.isVisited(i))
                GraphTraversal.dfs(graph, i, pass1, u -> {}, pass1::recordFinished);
        }

        Graph reversed = graph.reverse();
        TraversalState pass2 = new TraversalState(V);
        List<List<Integer>> sccs = new ArrayList<>();

        while (pass1.hasPending()) {
            int u = pass1.popNext();
            if (!pass2.isVisited(u)) {
                List<Integer> component = new ArrayList<>();
                GraphTraversal.dfs(reversed, u, pass2, component::add, u2 -> {});
                sccs.add(component);
            }
        }

        return sccs;
    }
}

class SCCRunner {
    private final SCCAlgorithm algorithm;
    private final Consumer<String> output;

    SCCRunner(SCCAlgorithm algorithm, Consumer<String> output) {
        this.algorithm = algorithm;
        this.output = output;
    }

    void run(Graph graph) {
        List<List<Integer>> sccs = algorithm.findSCCs(graph);
        output.accept("Strongly Connected Components:");
        for (int i = 0; i < sccs.size() - 1; i++) {
            StringBuilder sb = new StringBuilder();
            for (int v : sccs.get(i)) {
                sb.append(v).append(' ');
            }
            output.accept(sb.toString().trim());
        }
    }
}

class StronglyConnectedComponents {
    public static void main(String[] args) {
        int V = 5;
        int[][] edges = {
            {1, 3}, {1, 4}, {2, 1}, {3, 2}, {4, 5}
        };

        Graph graph = GraphFactory.fromEdges(edges, V + 1);
        new SCCRunner(new KosarajuSCC(), System.out::println).run(graph);
    }
}
