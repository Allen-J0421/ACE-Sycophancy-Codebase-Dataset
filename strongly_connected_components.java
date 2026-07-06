import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
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

    Iterable<Integer> neighbors(int u) {
        return () -> new AdjacencyIterator(adj[u], degree[u]);
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

    private static class AdjacencyIterator implements Iterator<Integer> {
        private final int[] data;
        private final int limit;
        private int cursor;

        AdjacencyIterator(int[] data, int limit) {
            this.data = data;
            this.limit = limit;
        }

        @Override public boolean hasNext() { return cursor < limit; }
        @Override public Integer next() { return data[cursor++]; }
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

class GraphTraversal {
    static void dfs(Graph graph, int start, boolean[] visited,
                    IntConsumer onEnter, IntConsumer onExit) {
        visited[start] = true;
        onEnter.accept(start);
        for (int v : graph.neighbors(start)) {
            if (!visited[v]) dfs(graph, v, visited, onEnter, onExit);
        }
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
        boolean[] visited = new boolean[V];
        Stack<Integer> finishOrder = new Stack<>();

        for (int i = 0; i < V; i++) {
            if (!visited[i])
                GraphTraversal.dfs(graph, i, visited, u -> {}, finishOrder::push);
        }

        Graph reversed = graph.reverse();
        boolean[] visited2 = new boolean[V];
        List<List<Integer>> sccs = new ArrayList<>();

        while (!finishOrder.isEmpty()) {
            int u = finishOrder.pop();
            if (!visited2[u]) {
                List<Integer> component = new ArrayList<>();
                GraphTraversal.dfs(reversed, u, visited2, component::add, u2 -> {});
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
