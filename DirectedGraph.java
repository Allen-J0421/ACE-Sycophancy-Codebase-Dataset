import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DirectedGraph implements Graph {
    private final List<Vertex> vertices;
    private final Map<Vertex, List<Vertex>> adjacencyByVertex;

    private DirectedGraph(List<Vertex> vertices, Map<Vertex, List<Vertex>> adjacencyByVertex) {
        this.vertices = vertices;
        this.adjacencyByVertex = adjacencyByVertex;
    }

    public static DirectedGraph fromEdges(int vertexCount, Edge... edges) {
        return fromEdges(createRangeVertices(vertexCount), List.of(edges));
    }

    public static DirectedGraph fromEdges(int vertexCount, List<Edge> edges) {
        return fromEdges(createRangeVertices(vertexCount), edges);
    }

    public static DirectedGraph fromEdges(List<Vertex> vertices, Edge... edges) {
        return fromEdges(vertices, List.of(edges));
    }

    public static DirectedGraph fromEdges(List<Vertex> vertices, List<Edge> edges) {
        List<Vertex> normalizedVertices = normalizeVertices(vertices);

        if (edges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        Map<Vertex, List<Vertex>> adjacencyByVertex = createEmptyAdjacency(normalizedVertices);

        for (Edge edge : edges) {
            validateEdge(edge);

            Vertex from = edge.from();
            Vertex to = edge.to();
            validateVertex(from, adjacencyByVertex);
            validateVertex(to, adjacencyByVertex);
            adjacencyByVertex.get(from).add(to);
        }

        return new DirectedGraph(normalizedVertices, freeze(adjacencyByVertex));
    }

    @Override
    public int vertexCount() {
        return vertices.size();
    }

    @Override
    public List<Vertex> vertices() {
        return vertices;
    }

    @Override
    public List<Vertex> neighborsOf(Vertex vertex) {
        validateVertex(vertex, adjacencyByVertex);
        return adjacencyByVertex.get(vertex);
    }

    @Override
    public boolean containsVertex(Vertex vertex) {
        return adjacencyByVertex.containsKey(vertex);
    }

    @Override
    public DirectedGraph reverse() {
        Map<Vertex, List<Vertex>> reversedAdjacency = createEmptyAdjacency(vertices);

        for (Vertex vertex : vertices) {
            for (Vertex neighbor : neighborsOf(vertex)) {
                reversedAdjacency.get(neighbor).add(vertex);
            }
        }

        return new DirectedGraph(vertices, freeze(reversedAdjacency));
    }

    private static Map<Vertex, List<Vertex>> createEmptyAdjacency(List<Vertex> vertices) {
        Map<Vertex, List<Vertex>> adjacencyByVertex = new LinkedHashMap<>();
        for (Vertex vertex : vertices) {
            adjacencyByVertex.put(vertex, new ArrayList<>());
        }
        return adjacencyByVertex;
    }

    private static Map<Vertex, List<Vertex>> freeze(Map<Vertex, List<Vertex>> adjacencyByVertex) {
        Map<Vertex, List<Vertex>> immutableAdjacency = new LinkedHashMap<>();

        for (Map.Entry<Vertex, List<Vertex>> entry : adjacencyByVertex.entrySet()) {
            immutableAdjacency.put(entry.getKey(), List.copyOf(entry.getValue()));
        }

        return Collections.unmodifiableMap(immutableAdjacency);
    }

    private static List<Vertex> createRangeVertices(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }

        List<Vertex> vertices = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            vertices.add(Vertex.of(vertex));
        }
        return Collections.unmodifiableList(vertices);
    }

    private static List<Vertex> normalizeVertices(List<Vertex> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("Vertices must not be null.");
        }

        Set<Vertex> uniqueVertices = new LinkedHashSet<>();
        for (Vertex vertex : vertices) {
            if (vertex == null) {
                throw new IllegalArgumentException("Vertices must not contain null values.");
            }

            if (!uniqueVertices.add(vertex)) {
                throw new IllegalArgumentException("Vertices must not contain duplicates.");
            }
        }

        return List.copyOf(uniqueVertices);
    }

    private static void validateEdge(Edge edge) {
        if (edge == null || edge.from() == null || edge.to() == null) {
            throw new IllegalArgumentException("Edges must not contain null values.");
        }
    }

    private static void validateVertex(Vertex vertex, Map<Vertex, List<Vertex>> adjacencyByVertex) {
        if (!adjacencyByVertex.containsKey(vertex)) {
            throw new IllegalArgumentException("Vertex " + vertex + " is not part of the graph.");
        }
    }
}
