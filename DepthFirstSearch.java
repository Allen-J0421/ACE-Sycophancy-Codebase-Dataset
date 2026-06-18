import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DepthFirstSearch {

    private DepthFirstSearch() {
        // Utility class.
    }

    public static List<Integer> dfs(List<? extends List<Integer>> adjacencyList) {
        Objects.requireNonNull(adjacencyList, "adjacencyList");

        boolean[] visited = new boolean[adjacencyList.size()];
        List<Integer> traversal = new ArrayList<>(adjacencyList.size());

        for (int vertex = 0; vertex < adjacencyList.size(); vertex++) {
            if (!visited[vertex]) {
                dfsFromVertex(adjacencyList, visited, vertex, traversal);
            }
        }

        return traversal;
    }

    private static void dfsFromVertex(
            List<? extends List<Integer>> adjacencyList,
            boolean[] visited,
            int vertex,
            List<Integer> traversal) {
        visited[vertex] = true;
        traversal.add(vertex);

        for (int neighbor : adjacencyList.get(vertex)) {
            if (neighbor < 0 || neighbor >= adjacencyList.size()) {
                throw new IllegalArgumentException("Invalid neighbor vertex: " + neighbor);
            }
            if (!visited[neighbor]) {
                dfsFromVertex(adjacencyList, visited, neighbor, traversal);
            }
        }
    }

    static void addEdge(List<List<Integer>> adjacencyList, int u, int v) {
        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    private static List<List<Integer>> createGraph(int vertices) {
        List<List<Integer>> adjacencyList = new ArrayList<>(vertices);
        for (int vertex = 0; vertex < vertices; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
        return adjacencyList;
    }

    public static void main(String[] args) {
        List<List<Integer>> adjacencyList = createGraph(6);

        addEdge(adjacencyList, 1, 2);
        addEdge(adjacencyList, 0, 3);
        addEdge(adjacencyList, 2, 0);
        addEdge(adjacencyList, 5, 4);

        List<Integer> traversal = dfs(adjacencyList);

        for (int vertex : traversal) {
            System.out.print(vertex + " ");
        }
    }
}
