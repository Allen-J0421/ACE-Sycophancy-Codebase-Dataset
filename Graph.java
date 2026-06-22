import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

record Graph(int vertexCount, List<Edge> edges) {
    Graph {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }

        if (edges == null) {
            throw new IllegalArgumentException("Edges cannot be null.");
        }

        edges = List.copyOf(edges);
    }

    int requiredEdgeCount() {
        return Math.max(0, vertexCount - 1);
    }

    boolean isTriviallyConnected() {
        return vertexCount <= 1;
    }

    List<Edge> edgesSortedByWeight() {
        List<Edge> sortedEdges = new ArrayList<>(edges);
        sortedEdges.sort(Comparator.comparingInt(Edge::weight));
        return sortedEdges;
    }
}
