import java.util.Arrays;

final class GraphFactory {
    private GraphFactory() {
        // Factory class.
    }

    static Graph fromEdgeMatrix(int vertexCount, int[][] rawEdges) {
        if (rawEdges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        return new Graph(vertexCount, Arrays.stream(rawEdges)
            .map(Edge::fromMatrixRow)
            .toList());
    }
}
