import java.util.ArrayList;
import java.util.List;

public class DepthFirstSearch {

    static class Graph {
        private final int vertices;
        private final List<List<Integer>> adj;

        Graph(int vertices) {
            this.vertices = vertices;
            this.adj = new ArrayList<>();
            for (int i = 0; i < vertices; i++) {
                adj.add(new ArrayList<>());
            }
        }

        void addEdge(int u, int v) {
            adj.get(u).add(v);
            adj.get(v).add(u);
        }

        int size() {
            return vertices;
        }

        List<Integer> neighbors(int node) {
            return adj.get(node);
        }
    }

    private static void dfsRec(Graph graph, boolean[] visited, int node, List<Integer> result) {
        visited[node] = true;
        result.add(node);

        for (int neighbor : graph.neighbors(node)) {
            if (!visited[neighbor]) {
                dfsRec(graph, visited, neighbor, result);
            }
        }
    }

    public static List<Integer> dfs(Graph graph) {
        boolean[] visited = new boolean[graph.size()];
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < graph.size(); i++) {
            if (!visited[i]) {
                dfsRec(graph, visited, i, result);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);

        List<Integer> result = dfs(graph);

        for (int node : result) {
            System.out.print(node + " ");
        }
    }
}
