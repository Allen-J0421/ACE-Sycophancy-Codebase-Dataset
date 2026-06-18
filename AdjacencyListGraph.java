import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class AdjacencyListGraph implements MutableGraph {
    private final List<List<Integer>> adjacencyList;
    private final List<List<Integer>> adjacencyViews;

    AdjacencyListGraph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }

        adjacencyList = new ArrayList<>(vertexCount);
        adjacencyViews = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            List<Integer> neighbors = new ArrayList<>();
            adjacencyList.add(neighbors);
            adjacencyViews.add(Collections.unmodifiableList(neighbors));
        }
    }

    @Override
    public int vertexCount() {
        return adjacencyList.size();
    }

    @Override
    public void addUndirectedEdge(int from, int to) {
        validateVertex(from);
        validateVertex(to);

        addDirectedEdge(from, to);
        addDirectedEdge(to, from);
    }

    @Override
    public List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return adjacencyViews.get(vertex);
    }

    private void addDirectedEdge(int from, int to) {
        adjacencyList.get(from).add(to);
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("Vertex out of range: " + vertex);
        }
    }
}
