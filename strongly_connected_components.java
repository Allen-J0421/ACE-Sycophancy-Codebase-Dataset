import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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

class KosarajuSCC {
    private final Graph graph;

    KosarajuSCC(Graph graph) {
        this.graph = graph;
    }

    List<List<Integer>> findSCCs() {
        int V = graph.vertexCount();
        boolean[] visited = new boolean[V];
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < V; i++) {
            if (!visited[i]) dfsFirst(i, visited, stack);
        }

        Graph reversed = graph.reverse();
        boolean[] visited2 = new boolean[V];
        List<List<Integer>> sccs = new ArrayList<>();

        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (!visited2[u]) {
                List<Integer> component = new ArrayList<>();
                dfsSecond(u, reversed, visited2, component);
                sccs.add(component);
            }
        }

        return sccs;
    }

    private void dfsFirst(int u, boolean[] visited, Stack<Integer> stack) {
        visited[u] = true;
        for (int v : graph.neighbors(u)) {
            if (!visited[v]) dfsFirst(v, visited, stack);
        }
        stack.push(u);
    }

    private void dfsSecond(int u, Graph reversed, boolean[] visited, List<Integer> component) {
        visited[u] = true;
        component.add(u);
        for (int v : reversed.neighbors(u)) {
            if (!visited[v]) dfsSecond(v, reversed, visited, component);
        }
    }
}

class StronglyConnectedComponents {
    public static void main(String[] args) {
        int V = 5;
        int[][] edges = {
            {1, 3}, {1, 4}, {2, 1}, {3, 2}, {4, 5}
        };

        Graph graph = new Graph(V + 1);
        for (int[] edge : edges) {
            graph.addEdge(edge[0], edge[1]);
        }

        List<List<Integer>> sccs = new KosarajuSCC(graph).findSCCs();

        System.out.println("Strongly Connected Components:");
        for (int i = 0; i < sccs.size() - 1; i++) {
            for (int v : sccs.get(i)) {
                System.out.print(v + " ");
            }
            System.out.println();
        }
    }
}
