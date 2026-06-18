import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Graph {
    private final List<List<Integer>> adjacencyList;
    private final List<List<Integer>> neighborViews;

    private Graph(int vertexCount) {
        adjacencyList = new ArrayList<>(vertexCount);
        neighborViews = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            List<Integer> neighbors = new ArrayList<>();
            adjacencyList.add(neighbors);
            neighborViews.add(Collections.unmodifiableList(neighbors));
        }
    }

    public static Graph create(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }
        return new Graph(vertexCount);
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public boolean containsVertex(int vertex) {
        return vertex >= 0 && vertex < adjacencyList.size();
    }

    public void addUndirectedEdge(int u, int v) {
        validateVertex(u);
        validateVertex(v);

        connectVertices(u, v);
        connectVertices(v, u);
    }

    public void addDirectedEdge(int from, int to) {
        validateVertex(from);
        validateVertex(to);

        connectVertices(from, to);
    }

    public List<Integer> neighbors(int vertex) {
        validateVertex(vertex);
        return neighborViews.get(vertex);
    }

    private void connectVertices(int from, int to) {
        adjacencyList.get(from).add(to);
    }

    private void validateVertex(int vertex) {
        if (!containsVertex(vertex)) {
            throw new IllegalArgumentException("vertex out of bounds: " + vertex);
        }
    }
}
