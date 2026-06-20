import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class GraphValidation {
    private GraphValidation() {
    }

    static void requireVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }
    }

    static int[] requireEdgeVertices(int[] edge, int vertexCount, int edgeIndex) {
        int[] nonNullEdge = Objects.requireNonNull(edge, "edge at index " + edgeIndex);
        if (nonNullEdge.length != 2) {
            throw new IllegalArgumentException("edge at index " + edgeIndex + " must contain exactly two vertices");
        }

        requireVertex(nonNullEdge[0], vertexCount, "edge[" + edgeIndex + "][0]");
        requireVertex(nonNullEdge[1], vertexCount, "edge[" + edgeIndex + "][1]");
        return nonNullEdge;
    }

    static void requireVertex(int vertex, int vertexCount, String label) {
        if (vertex < 0 || vertex >= vertexCount) {
            if (vertexCount == 0) {
                throw new IllegalArgumentException(label + " cannot be used when the graph has no vertices");
            }

            throw new IllegalArgumentException(label + " must be between 0 and " + (vertexCount - 1));
        }
    }

    static List<List<Integer>> freezeAdjacencyList(List<List<Integer>> adjacencyList) {
        List<List<Integer>> immutableAdjacencyList = new ArrayList<>(adjacencyList.size());
        for (List<Integer> neighbors : adjacencyList) {
            immutableAdjacencyList.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
        }

        return Collections.unmodifiableList(immutableAdjacencyList);
    }
}
