import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class UndirectedGraphFactory {

    private UndirectedGraphFactory() {
    }

    static UndirectedGraph fromEdgeList(int vertexCount, int[][] edges) {
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

        List<List<Integer>> frozenAdjacencyList = new ArrayList<>(vertexCount);
        for (List<Integer> neighbors : adjacencyList) {
            frozenAdjacencyList.add(List.copyOf(neighbors));
        }

        return new UndirectedGraph(vertexCount, frozenAdjacencyList);
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
