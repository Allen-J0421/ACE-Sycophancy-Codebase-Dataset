import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DirectedGraphBuilder {
    private final Set<Vertex> vertices = new LinkedHashSet<>();
    private final List<Edge> edges = new ArrayList<>();

    public DirectedGraphBuilder addVertex(Vertex vertex) {
        validateVertex(vertex);
        vertices.add(vertex);
        return this;
    }

    public DirectedGraphBuilder addVertices(List<Vertex> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("Vertices must not be null.");
        }

        for (Vertex vertex : vertices) {
            addVertex(vertex);
        }

        return this;
    }

    public DirectedGraphBuilder addVertexRange(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }

        for (int vertexId = 0; vertexId < vertexCount; vertexId++) {
            addVertex(Vertex.of(vertexId));
        }

        return this;
    }

    public DirectedGraphBuilder addEdge(Edge edge) {
        validateEdge(edge);
        edges.add(edge);
        return this;
    }

    public DirectedGraphBuilder addEdges(List<Edge> edges) {
        if (edges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        for (Edge edge : edges) {
            addEdge(edge);
        }

        return this;
    }

    public DirectedGraph build() {
        Map<Vertex, List<Vertex>> adjacencyByVertex = createEmptyAdjacency();

        for (Edge edge : edges) {
            Vertex from = edge.from();
            Vertex to = edge.to();

            validateContainedVertex(from);
            validateContainedVertex(to);
            adjacencyByVertex.get(from).add(to);
        }

        return DirectedGraph.fromAdjacency(List.copyOf(vertices), adjacencyByVertex);
    }

    private Map<Vertex, List<Vertex>> createEmptyAdjacency() {
        Map<Vertex, List<Vertex>> adjacencyByVertex = new LinkedHashMap<>();
        for (Vertex vertex : vertices) {
            adjacencyByVertex.put(vertex, new ArrayList<>());
        }
        return adjacencyByVertex;
    }

    private void validateContainedVertex(Vertex vertex) {
        if (!vertices.contains(vertex)) {
            throw new IllegalArgumentException("Vertex " + vertex + " is not part of the graph.");
        }
    }

    private static void validateVertex(Vertex vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Vertices must not contain null values.");
        }
    }

    private static void validateEdge(Edge edge) {
        if (edge == null || edge.from() == null || edge.to() == null) {
            throw new IllegalArgumentException("Edges must not contain null values.");
        }
    }
}
