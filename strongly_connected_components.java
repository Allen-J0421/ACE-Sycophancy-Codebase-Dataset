import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@FunctionalInterface
interface NeighborProvider {
    void neighbors(int u, IntConsumer action);
}

class Graph implements NeighborProvider {
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

    @Override
    public void neighbors(int u, IntConsumer action) {
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
    private final int[] finishOrder;
    private final int finishCount;

    private TraversalState(boolean[] visited, int[] finishOrder, int finishCount) {
        this.visited = visited;
        this.finishOrder = finishOrder;
        this.finishCount = finishCount;
    }

    static TraversalState empty(int vertexCount) {
        return new TraversalState(new boolean[vertexCount], new int[vertexCount], 0);
    }

    boolean isVisited(int u) { return visited[u]; }

    TraversalState withVisited(int u) {
        boolean[] next = Arrays.copyOf(visited, visited.length);
        next[u] = true;
        return new TraversalState(next, finishOrder, finishCount);
    }

    TraversalState withFinished(int u) {
        int[] next = Arrays.copyOf(finishOrder, finishOrder.length);
        next[finishCount] = u;
        return new TraversalState(visited, next, finishCount + 1);
    }

    boolean hasPending() { return finishCount > 0; }
    int peekNext()       { return finishOrder[finishCount - 1]; }

    TraversalState withPopped() {
        return new TraversalState(visited, finishOrder, finishCount - 1);
    }
}

@FunctionalInterface
interface StateTransition {
    TraversalState apply(TraversalState state, int vertex);
}

class GraphTraversal {
    static TraversalState dfs(NeighborProvider provider, int start, TraversalState state,
                               StateTransition onEnter, StateTransition onExit) {
        state = state.withVisited(start);
        state = onEnter.apply(state, start);
        TraversalState[] current = {state};
        provider.neighbors(start, v -> {
            if (!current[0].isVisited(v))
                current[0] = dfs(provider, v, current[0], onEnter, onExit);
        });
        return onExit.apply(current[0], start);
    }
}

interface SCCAlgorithm {
    List<List<Integer>> findSCCs(Graph graph);
}

class KosarajuSCC implements SCCAlgorithm {

    @Override
    public List<List<Integer>> findSCCs(Graph graph) {
        int V = graph.vertexCount();
        TraversalState pass1 = TraversalState.empty(V);

        for (int i = 0; i < V; i++) {
            if (!pass1.isVisited(i))
                pass1 = GraphTraversal.dfs(graph, i, pass1,
                    (s, u) -> s,
                    (s, u) -> s.withFinished(u));
        }

        Graph reversed = graph.reverse();
        TraversalState pass2 = TraversalState.empty(V);
        List<List<Integer>> sccs = new ArrayList<>();

        while (pass1.hasPending()) {
            int u = pass1.peekNext();
            pass1 = pass1.withPopped();
            if (!pass2.isVisited(u)) {
                List<Integer> component = new ArrayList<>();
                pass2 = GraphTraversal.dfs(reversed, u, pass2,
                    (s, v) -> { component.add(v); return s; },
                    (s, v) -> s);
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
