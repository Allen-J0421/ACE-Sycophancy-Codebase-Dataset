import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DirectedGraph implements Graph {
    private final List<Vertex> vertices;
    private final Map<Vertex, List<Vertex>> adjacencyByVertex;

    private DirectedGraph(List<Vertex> vertices, Map<Vertex, List<Vertex>> adjacencyByVertex) {
        this.vertices = vertices;
        this.adjacencyByVertex = adjacencyByVertex;
    }

    public static DirectedGraphBuilder builder() {
        return new DirectedGraphBuilder();
    }

    public static DirectedGraph fromEdges(int vertexCount, Edge... edges) {
        return builder()
            .addVertexRange(vertexCount)
            .addEdges(List.of(edges))
            .build();
    }

    public static DirectedGraph fromEdges(int vertexCount, List<Edge> edges) {
        return builder()
            .addVertexRange(vertexCount)
            .addEdges(edges)
            .build();
    }

    public static DirectedGraph fromEdges(List<Vertex> vertices, Edge... edges) {
        return builder()
            .addVertices(vertices)
            .addEdges(List.of(edges))
            .build();
    }

    public static DirectedGraph fromEdges(List<Vertex> vertices, List<Edge> edges) {
        return builder()
            .addVertices(vertices)
            .addEdges(edges)
            .build();
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

    static DirectedGraph fromAdjacency(List<Vertex> vertices, Map<Vertex, List<Vertex>> adjacencyByVertex) {
        validateVertices(vertices);
        validateAdjacency(vertices, adjacencyByVertex);
        return new DirectedGraph(List.copyOf(vertices), freeze(adjacencyByVertex));
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

    private static void validateVertices(List<Vertex> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("Vertices must not be null.");
        }

        for (Vertex vertex : vertices) {
            if (vertex == null) {
                throw new IllegalArgumentException("Vertices must not contain null values.");
            }
        }
    }

    private static void validateAdjacency(
        List<Vertex> vertices,
        Map<Vertex, List<Vertex>> adjacencyByVertex
    ) {
        if (adjacencyByVertex == null) {
            throw new IllegalArgumentException("Adjacency must not be null.");
        }

        for (Vertex vertex : vertices) {
            validateVertex(vertex, adjacencyByVertex);
        }
    }

    private static void validateVertex(Vertex vertex, Map<Vertex, List<Vertex>> adjacencyByVertex) {
        if (!adjacencyByVertex.containsKey(vertex)) {
            throw new IllegalArgumentException("Vertex " + vertex + " is not part of the graph.");
        }
    }
}
