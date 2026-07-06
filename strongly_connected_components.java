import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

class Graph {
    private final int vertices;
    private final List<List<Integer>> adj;

    Graph(int vertices) {
        this.vertices = vertices;
        adj = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adj.add(new ArrayList<>());
        }
    }

    void addEdge(int u, int v) {
        adj.get(u).add(v);
    }

    List<Integer> neighbors(int u) {
        return adj.get(u);
    }

    int vertexCount() {
        return vertices;
    }

    Graph reverse() {
        Graph rev = new Graph(vertices);
        for (int u = 0; u < vertices; u++) {
            for (int v : adj.get(u)) {
                rev.addEdge(v, u);
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

interface SCCAlgorithm {
    List<List<Integer>> findSCCs(Graph graph);
}

class KosarajuSCC implements SCCAlgorithm {

    @Override
    public List<List<Integer>> findSCCs(Graph graph) {
        int V = graph.vertexCount();
        boolean[] visited = new boolean[V];
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < V; i++) {
            if (!visited[i]) dfsFirst(graph, i, visited, stack);
        }

        Graph reversed = graph.reverse();
        boolean[] visited2 = new boolean[V];
        List<List<Integer>> sccs = new ArrayList<>();

        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (!visited2[u]) {
                List<Integer> component = new ArrayList<>();
                dfsSecond(reversed, u, visited2, component);
                sccs.add(component);
            }
        }

        return sccs;
    }

    private void dfsFirst(Graph graph, int u, boolean[] visited, Stack<Integer> stack) {
        visited[u] = true;
        for (int v : graph.neighbors(u)) {
            if (!visited[v]) dfsFirst(graph, v, visited, stack);
        }
        stack.push(u);
    }

    private void dfsSecond(Graph graph, int u, boolean[] visited, List<Integer> component) {
        visited[u] = true;
        component.add(u);
        for (int v : graph.neighbors(u)) {
            if (!visited[v]) dfsSecond(graph, v, visited, component);
        }
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
