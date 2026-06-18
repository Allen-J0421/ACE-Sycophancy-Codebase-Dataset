import java.util.ArrayDeque;
import java.util.ArrayList;
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
        return adj.get(node);
    }

    List<Integer> dfs() {
        boolean[] visited = new boolean[vertices];
        List<Integer> result = new ArrayList<>();

        for (int start = 0; start < vertices; start++) {
            if (!visited[start]) {
                result.addAll(traverseFrom(start, visited));
            }
        }

        return result;
    }

    private List<Integer> traverseFrom(int start, boolean[] visited) {
        List<Integer> component = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);
        visited[start] = true;

        while (!stack.isEmpty()) {
            int node = stack.pop();
            component.add(node);

            for (int neighbor : neighbors(node)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    stack.push(neighbor);
                }
            }
        }

        return component;
    }
}

public class DepthFirstSearch {

    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);

        List<Integer> result = graph.dfs();

        for (int node : result) {
            System.out.print(node + " ");
        }
    }
}
