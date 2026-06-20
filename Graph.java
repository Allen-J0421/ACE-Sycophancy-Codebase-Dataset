import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class Graph {

    private final int vertexCount;
    private final List<List<Integer>> adjacencyList;

    private Graph(int vertexCount, List<List<Integer>> adjacencyList) {
        this.vertexCount = vertexCount;
        this.adjacencyList = adjacencyList;
    }

    static Graph fromEdgeList(int vertexCount, int[][] edges) {
        validateGraphInput(vertexCount, edges);

        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            validateEdge(edge);

            int source = edge[0];
            int target = edge[1];
            validateVertex(source, vertexCount);
            validateVertex(target, vertexCount);

            adjacencyList.get(source).add(target);
            adjacencyList.get(target).add(source);
        }

        for (int i = 0; i < adjacencyList.size(); i++) {
            adjacencyList.set(i, List.copyOf(adjacencyList.get(i)));
        }

        return new Graph(vertexCount, List.copyOf(adjacencyList));
    }

    int vertexCount() {
        return vertexCount;
    }

    List<Integer> neighborsOf(int vertex) {
        return adjacencyList.get(vertex);
    }

    private static void validateGraphInput(int vertexCount, int[][] edges) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }
        Objects.requireNonNull(edges, "edges");
    }

    private static void validateEdge(int[] edge) {
        if (edge == null || edge.length != 2) {
            throw new IllegalArgumentException("Each edge must contain exactly two vertices");
        }
    }

    private static void validateVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                    "Vertex " + vertex + " is out of bounds for graph size " + vertexCount);
        }
    }
}
