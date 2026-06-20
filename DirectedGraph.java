import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DirectedGraph implements Graph {
    private final List<Integer> vertices;
    private final Map<Integer, List<Integer>> adjacencyByVertex;

    private DirectedGraph(List<Integer> vertices, Map<Integer, List<Integer>> adjacencyByVertex) {
        this.vertices = vertices;
        this.adjacencyByVertex = adjacencyByVertex;
    }

    public static DirectedGraph fromEdges(int vertexCount, Edge... edges) {
        return fromEdges(createRangeVertices(vertexCount), List.of(edges));
    }

    public static DirectedGraph fromEdges(int vertexCount, List<Edge> edges) {
        return fromEdges(createRangeVertices(vertexCount), edges);
    }

    public static DirectedGraph fromEdges(List<Integer> vertices, Edge... edges) {
        return fromEdges(vertices, List.of(edges));
    }

    public static DirectedGraph fromEdges(List<Integer> vertices, List<Edge> edges) {
        List<Integer> normalizedVertices = normalizeVertices(vertices);

        if (edges == null) {
            throw new IllegalArgumentException("Edges must not be null.");
        }

        Map<Integer, List<Integer>> adjacencyByVertex = createEmptyAdjacency(normalizedVertices);

        for (Edge edge : edges) {
            validateEdge(edge);

            int from = edge.from();
            int to = edge.to();
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
    public List<Integer> vertices() {
        return vertices;
    }

    @Override
    public List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex, adjacencyByVertex);
        return adjacencyByVertex.get(vertex);
    }

    @Override
    public boolean containsVertex(int vertex) {
        return adjacencyByVertex.containsKey(vertex);
    }

    @Override
    public DirectedGraph reverse() {
        Map<Integer, List<Integer>> reversedAdjacency = createEmptyAdjacency(vertices);

        for (int vertex : vertices) {
            for (int neighbor : neighborsOf(vertex)) {
                reversedAdjacency.get(neighbor).add(vertex);
            }
        }

        return new DirectedGraph(vertices, freeze(reversedAdjacency));
    }

    private static Map<Integer, List<Integer>> createEmptyAdjacency(List<Integer> vertices) {
        Map<Integer, List<Integer>> adjacencyByVertex = new LinkedHashMap<>();
        for (int vertex : vertices) {
            adjacencyByVertex.put(vertex, new ArrayList<>());
        }
        return adjacencyByVertex;
    }

    private static Map<Integer, List<Integer>> freeze(Map<Integer, List<Integer>> adjacencyByVertex) {
        Map<Integer, List<Integer>> immutableAdjacency = new LinkedHashMap<>();

        for (Map.Entry<Integer, List<Integer>> entry : adjacencyByVertex.entrySet()) {
            immutableAdjacency.put(entry.getKey(), List.copyOf(entry.getValue()));
        }

        return Collections.unmodifiableMap(immutableAdjacency);
    }

    private static List<Integer> createRangeVertices(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }

        List<Integer> vertices = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            vertices.add(vertex);
        }
        return Collections.unmodifiableList(vertices);
    }

    private static List<Integer> normalizeVertices(List<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("Vertices must not be null.");
        }

        Set<Integer> uniqueVertices = new LinkedHashSet<>();
        for (Integer vertex : vertices) {
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
        if (edge == null) {
            throw new IllegalArgumentException("Edges must not contain null values.");
        }
    }

    private static void validateVertex(int vertex, Map<Integer, List<Integer>> adjacencyByVertex) {
        if (!adjacencyByVertex.containsKey(vertex)) {
            throw new IllegalArgumentException("Vertex " + vertex + " is not part of the graph.");
        }
    }
}
