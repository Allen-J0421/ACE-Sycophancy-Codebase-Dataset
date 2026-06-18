import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DepthFirstSearch {

    private DepthFirstSearch() {
    }

    private static void traverseComponent(List<List<Integer>> adjacencyList,
                                          int vertex,
                                          boolean[] visited,
                                          List<Integer> traversal) {
        visited[vertex] = true;
        traversal.add(vertex);

        for (int neighbor : adjacencyList.get(vertex)) {
            if (!visited[neighbor]) {
                traverseComponent(adjacencyList, neighbor, visited, traversal);
            }
        }
    }

    public static List<Integer> dfs(List<List<Integer>> adjacencyList) {
        Objects.requireNonNull(adjacencyList, "adjacencyList");

        boolean[] visited = new boolean[adjacencyList.size()];
        List<Integer> traversal = new ArrayList<>();

        for (int vertex = 0; vertex < adjacencyList.size(); vertex++) {
            if (!visited[vertex]) {
                traverseComponent(adjacencyList, vertex, visited, traversal);
            }
        }

        return traversal;
    }

    static List<List<Integer>> createGraph(int vertices) {
        if (vertices < 0) {
            throw new IllegalArgumentException("vertices must be non-negative");
        }

        List<List<Integer>> adjacencyList = new ArrayList<>(vertices);
        for (int vertex = 0; vertex < vertices; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }

        return adjacencyList;
    }

    static void addUndirectedEdge(List<List<Integer>> adjacencyList, int from, int to) {
        validateVertex(adjacencyList, from);
        validateVertex(adjacencyList, to);

        adjacencyList.get(from).add(to);
        adjacencyList.get(to).add(from);
    }

    private static void validateVertex(List<List<Integer>> adjacencyList, int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("vertex out of bounds: " + vertex);
        }
    }

    public static void main(String[] args) {
        int vertices = 6;
        List<List<Integer>> adjacencyList = createGraph(vertices);

        addUndirectedEdge(adjacencyList, 1, 2);
        addUndirectedEdge(adjacencyList, 0, 3);
        addUndirectedEdge(adjacencyList, 2, 0);
        addUndirectedEdge(adjacencyList, 5, 4);

        List<Integer> traversal = dfs(adjacencyList);

        for (int vertex : traversal) {
            System.out.print(vertex + " ");
        }
    }
}
