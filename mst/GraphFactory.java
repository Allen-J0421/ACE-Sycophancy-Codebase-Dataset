package mst;

import java.util.Arrays;

public final class GraphFactory {
    private GraphFactory() {
        // Factory class.
    }

    public static Graph fromEdgeMatrix(int vertexCount, int[][] rawEdges) {
        if (rawEdges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        return new Graph(vertexCount, Arrays.stream(rawEdges)
            .map(Edge::fromMatrixRow)
            .toList());
    }
}
