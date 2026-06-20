import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class Graph {
    private final GraphType type;
    private final List<List<Integer>> adjacencyList;

    Graph(GraphType type, List<List<Integer>> adjacencyList) {
        this.type = Objects.requireNonNull(type, "type");
        this.adjacencyList = adjacencyList;
    }

    static Graph fromEdges(int vertexCount, int[][] edges) {
        Objects.requireNonNull(edges, "edges");

        for (int edgeIndex = 0; edgeIndex < edges.length; edgeIndex++) {
            int[] edge = Objects.requireNonNull(edges[edgeIndex], "edge at index " + edgeIndex);
            validateEdge(edge, vertexCount, edgeIndex);
        }

        GraphBuilder builder = GraphBuilder.undirected(vertexCount);
        for (int[] edge : edges) {
            builder.addEdge(edge[0], edge[1]);
        }

        return builder.build();
    }

    static GraphBuilder builder(int vertexCount, GraphType type) {
        return new GraphBuilder(vertexCount, type);
    }

    int vertexCount() {
        return adjacencyList.size();
    }

    GraphType type() {
        return type;
    }

    boolean isDirected() {
        return type == GraphType.DIRECTED;
    }

    boolean isUndirected() {
        return type == GraphType.UNDIRECTED;
    }

    List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return adjacencyList.get(vertex);
    }

    <Result> Result analyze(GraphAnalysis<Result> analysis) {
        Objects.requireNonNull(analysis, "analysis");
        return analysis.analyze(this);
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }
    }

    private static void validateEdge(int[] edge, int vertexCount, int edgeIndex) {
        validateVertexCount(vertexCount);
        if (edge.length != 2) {
            throw new IllegalArgumentException("edge at index " + edgeIndex + " must contain exactly two vertices");
        }

        validateVertexIndex(edge[0], vertexCount, "edge[" + edgeIndex + "][0]");
        validateVertexIndex(edge[1], vertexCount, "edge[" + edgeIndex + "][1]");
    }

    private void validateVertex(int vertex) {
        validateVertexIndex(vertex, vertexCount(), "vertex");
    }

    private static void validateVertexIndex(int vertex, int vertexCount, String label) {
        if (vertex < 0 || vertex >= vertexCount) {
            if (vertexCount == 0) {
                throw new IllegalArgumentException(label + " cannot be used when the graph has no vertices");
            }

            throw new IllegalArgumentException(label + " must be between 0 and " + (vertexCount - 1));
        }
    }

    private static List<List<Integer>> toUnmodifiableAdjacencyList(List<List<Integer>> adjacencyList) {
        List<List<Integer>> immutableAdjacencyList = new ArrayList<>(adjacencyList.size());
        for (List<Integer> neighbors : adjacencyList) {
            immutableAdjacencyList.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
        }

        return Collections.unmodifiableList(immutableAdjacencyList);
    }

    static List<List<Integer>> freezeAdjacencyList(List<List<Integer>> adjacencyList) {
        return toUnmodifiableAdjacencyList(adjacencyList);
    }
}
