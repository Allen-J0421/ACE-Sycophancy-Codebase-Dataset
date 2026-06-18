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
    public List<Integer> neighborsOf(int vertex) {
        requireVertex(vertex);
        return adjacencyViews.get(vertex);
    }

    @Override
    public void addDirectedEdge(int from, int to) {
        requireVertex(from);
        requireVertex(to);
        adjacencyList.get(from).add(to);
    }
}
