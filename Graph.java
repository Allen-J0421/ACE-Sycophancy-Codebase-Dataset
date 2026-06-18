import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph implements IGraph {
    private final List<List<Integer>> adjacencyList;
    private final int vertexCount;
    private int edgeCount;
    private final Logger logger;

    public Graph(int vertexCount) {
        this(vertexCount, new Logger.NoOpLogger());
    }

    public Graph(int vertexCount, Logger logger) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative");
        }
        this.vertexCount = vertexCount;
        this.adjacencyList = new ArrayList<>();
        this.edgeCount = 0;
        this.logger = logger;

        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        logger.debug("Graph created with " + vertexCount + " vertices");
    }

    @Override
    public void addEdge(int u, int v) {
        validateVertexIndex(u);
        validateVertexIndex(v);
        if (u == v) {
            throw new IllegalArgumentException("Self-loops are not allowed");
        }
        if (!hasEdge(u, v)) {
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
            edgeCount++;
            logger.debug("Added edge: " + u + " - " + v);
        }
    }

    @Override
    public void removeEdge(int u, int v) {
        validateVertexIndex(u);
        validateVertexIndex(v);
        if (adjacencyList.get(u).remove(Integer.valueOf(v))) {
            adjacencyList.get(v).remove(Integer.valueOf(u));
            edgeCount--;
            logger.debug("Removed edge: " + u + " - " + v);
        }
    }

    @Override
    public List<Integer> getNeighbors(int vertex) {
        validateVertexIndex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

    @Override
    public int getEdgeCount() {
        return edgeCount;
    }

    @Override
    public boolean hasEdge(int u, int v) {
        validateVertexIndex(u);
        validateVertexIndex(v);
        return adjacencyList.get(u).contains(v);
    }

    @Override
    public int getDegree(int vertex) {
        validateVertexIndex(vertex);
        return adjacencyList.get(vertex).size();
    }

    @Override
    public boolean isEmpty() {
        return vertexCount == 0;
    }

    @Override
    public List<Integer> getAllVertices() {
        List<Integer> vertices = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            vertices.add(i);
        }
        return vertices;
    }

    @Override
    public Set<Integer> getVerticesWithDegree(int degree) {
        Set<Integer> vertices = new HashSet<>();
        for (int i = 0; i < vertexCount; i++) {
            if (getDegree(i) == degree) {
                vertices.add(i);
            }
        }
        return vertices;
    }

    public GraphStatistics getStatistics() {
        return new GraphStatistics(this);
    }

    private void validateVertexIndex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IndexOutOfBoundsException(
                    String.format("Vertex %d is out of bounds [0, %d)", vertex, vertexCount));
        }
    }
}
