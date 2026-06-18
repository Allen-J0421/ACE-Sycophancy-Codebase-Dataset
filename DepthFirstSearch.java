import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public final class DepthFirstSearch {

    private DepthFirstSearch() {
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

    public static List<Integer> dfs(List<List<Integer>> adjacencyList) {
        validateGraph(adjacencyList);

        boolean[] visited = new boolean[adjacencyList.size()];
        List<Integer> traversal = new ArrayList<>();

        for (int vertex = 0; vertex < adjacencyList.size(); vertex++) {
            if (!visited[vertex]) {
                traverseComponent(adjacencyList, vertex, visited, traversal);
            }
        }

        return traversal;
    }

    public static List<Integer> dfsFromVertex(List<List<Integer>> adjacencyList, int startVertex) {
        validateGraph(adjacencyList);
        validateVertex(adjacencyList, startVertex);

        boolean[] visited = new boolean[adjacencyList.size()];
        List<Integer> traversal = new ArrayList<>();
        traverseComponent(adjacencyList, startVertex, visited, traversal);
        return traversal;
    }

    private static void traverseComponent(List<List<Integer>> adjacencyList,
                                          int startVertex,
                                          boolean[] visited,
                                          List<Integer> traversal) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(startVertex);

        while (!stack.isEmpty()) {
            int vertex = stack.pop();
            if (visited[vertex]) {
                continue;
            }

            visited[vertex] = true;
            traversal.add(vertex);

            List<Integer> neighbors = adjacencyList.get(vertex);
            for (int index = neighbors.size() - 1; index >= 0; index--) {
                int neighbor = neighbors.get(index);
                if (!visited[neighbor]) {
                    stack.push(neighbor);
                }
            }
        }
    }

    private static void validateGraph(List<List<Integer>> adjacencyList) {
        Objects.requireNonNull(adjacencyList, "adjacencyList");

        for (int vertex = 0; vertex < adjacencyList.size(); vertex++) {
            List<Integer> neighbors = Objects.requireNonNull(
                    adjacencyList.get(vertex),
                    "adjacencyList contains a null neighbor list at vertex " + vertex);

            for (int neighbor : neighbors) {
                if (neighbor < 0 || neighbor >= adjacencyList.size()) {
                    throw new IllegalArgumentException(
                            "neighbor out of bounds for vertex " + vertex + ": " + neighbor);
                }
            }
        }
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
