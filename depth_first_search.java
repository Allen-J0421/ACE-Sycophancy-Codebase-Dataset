import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

class Graph {
    private final List<List<Integer>> adj;

    private Graph(List<List<Integer>> adj) {
        this.adj = adj;
    }

    int size() {
        return adj.size();
    }

    List<Integer> neighbors(int node) {
        return adj.get(node);
    }

    static class Builder {
        private final List<List<Integer>> adj;

        Builder(int vertices) {
            this.adj = new ArrayList<>();
            for (int i = 0; i < vertices; i++) {
                adj.add(new ArrayList<>());
            }
        }

        Builder addEdge(int u, int v) {
            adj.get(u).add(v);
            adj.get(v).add(u);
            return this;
        }

        Graph build() {
            List<List<Integer>> frozen = new ArrayList<>();
            for (List<Integer> row : adj) {
                frozen.add(Collections.unmodifiableList(row));
            }
            return new Graph(Collections.unmodifiableList(frozen));
        }
    }
}

public class DepthFirstSearch {

    private DepthFirstSearch() {}

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
        Graph graph = new Graph.Builder(6)
                .addEdge(1, 2)
                .addEdge(0, 3)
                .addEdge(2, 0)
                .addEdge(5, 4)
                .build();

        List<Integer> result = dfs(graph);

        System.out.println(result.stream()
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.joining(" ")));
    }
}
