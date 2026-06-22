import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class PrimVertexState {
    private static final int NO_PARENT = -1;

    private final int[] parentByVertex;
    private final int[] bestWeightByVertex;
    private final boolean[] includedVertices;

    PrimVertexState(int vertexCount, int startVertex) {
        parentByVertex = new int[vertexCount];
        bestWeightByVertex = new int[vertexCount];
        includedVertices = new boolean[vertexCount];

        Arrays.fill(parentByVertex, NO_PARENT);
        Arrays.fill(bestWeightByVertex, Integer.MAX_VALUE);
        bestWeightByVertex[startVertex] = 0;
    }

    int nextVertexToInclude() {
        int lightestWeight = Integer.MAX_VALUE;
        int selectedVertex = -1;

        for (int vertex = 0; vertex < includedVertices.length; vertex++) {
            if (!includedVertices[vertex] && bestWeightByVertex[vertex] < lightestWeight) {
                lightestWeight = bestWeightByVertex[vertex];
                selectedVertex = vertex;
            }
        }

        return selectedVertex;
    }

    void include(int vertex) {
        includedVertices[vertex] = true;
    }

    boolean isIncluded(int vertex) {
        return includedVertices[vertex];
    }

    void considerEdge(int fromVertex, int toVertex, int weight) {
        if (weight < bestWeightByVertex[toVertex]) {
            parentByVertex[toVertex] = fromVertex;
            bestWeightByVertex[toVertex] = weight;
        }
    }

    MstResult buildResult() {
        List<Edge> edges = new ArrayList<>();

        for (int vertex = 1; vertex < parentByVertex.length; vertex++) {
            int parentVertex = parentByVertex[vertex];
            if (parentVertex == NO_PARENT) {
                throw new IllegalStateException("Graph must be connected.");
            }

            edges.add(new Edge(parentVertex, vertex, bestWeightByVertex[vertex]));
        }

        return MstResult.fromEdges(edges);
    }
}
