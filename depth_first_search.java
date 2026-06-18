import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

class Graph {
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
        return Collections.unmodifiableList(adj.get(node));
    }
}

public class DepthFirstSearch {

    public static List<Integer> dfs(Graph graph) {
        boolean[] visited = new boolean[graph.size()];
        List<Integer> result = new ArrayList<>();

        for (int start = 0; start < graph.size(); start++) {
            if (!visited[start]) {
                result.addAll(traverseFrom(graph, start, visited));
            }
        }

        return result;
    }

    private static List<Integer> traverseFrom(Graph graph, int start, boolean[] visited) {
        List<Integer> component = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);
        visited[start] = true;

        while (!stack.isEmpty()) {
            int node = stack.pop();
            component.add(node);

            for (int neighbor : graph.neighbors(node)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    stack.push(neighbor);
                }
            }
        }

        return component;
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
