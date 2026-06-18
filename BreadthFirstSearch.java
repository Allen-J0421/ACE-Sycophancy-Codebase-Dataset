import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public final class BreadthFirstSearch {

    private BreadthFirstSearch() {
        // Utility class.
    }

    private static void bfsComponent(List<List<Integer>> adjacencyList, int source, boolean[] visited, List<Integer> result) {
        Deque<Integer> queue = new ArrayDeque<>();
        visited[source] = true;
        queue.addLast(source);

        while (!queue.isEmpty()) {
            int current = queue.removeFirst();
            result.add(current);

            for (int neighbor : adjacencyList.get(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.addLast(neighbor);
                }
            }
        }
    }

    public static List<Integer> bfs(List<List<Integer>> adjacencyList) {
        Objects.requireNonNull(adjacencyList, "adjacencyList");

        int vertexCount = adjacencyList.size();
        boolean[] visited = new boolean[vertexCount];
        List<Integer> result = new ArrayList<>(vertexCount);

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!visited[vertex]) {
                bfsComponent(adjacencyList, vertex, visited, result);
            }
        }
        return result;
    }

    public static void addUndirectedEdge(List<List<Integer>> adjacencyList, int u, int v) {
        Objects.requireNonNull(adjacencyList, "adjacencyList");
        validateVertex(adjacencyList, u);
        validateVertex(adjacencyList, v);

        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    public static void addDirectedEdge(List<List<Integer>> adjacencyList, int from, int to) {
        Objects.requireNonNull(adjacencyList, "adjacencyList");
        validateVertex(adjacencyList, from);
        validateVertex(adjacencyList, to);

        adjacencyList.get(from).add(to);
    }

    private static void validateVertex(List<List<Integer>> adjacencyList, int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("vertex out of bounds: " + vertex);
        }
    }

    public static List<List<Integer>> createGraph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }

        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
        return adjacencyList;
    }

    public static void main(String[] args) {
        List<List<Integer>> adjacencyList = createGraph(6);

        addUndirectedEdge(adjacencyList, 1, 2);
        addUndirectedEdge(adjacencyList, 2, 0);
        addUndirectedEdge(adjacencyList, 0, 3);
        addUndirectedEdge(adjacencyList, 4, 5);

        List<Integer> traversal = bfs(adjacencyList);
        for (int vertex : traversal) {
            System.out.print(vertex + " ");
        }
    }
}
