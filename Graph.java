import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Graph {

    private final List<List<Integer>> adjacencyList;

    private Graph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public static Graph undirected(int vertices) {
        if (vertices < 0) {
            throw new IllegalArgumentException("vertices must be non-negative");
        }

        List<List<Integer>> adjacencyList = new ArrayList<>(vertices);
        for (int vertex = 0; vertex < vertices; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
        return new Graph(adjacencyList);
    }

    public int size() {
        return adjacencyList.size();
    }

    public List<Integer> neighborsOf(int vertex) {
        checkVertex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    public void addEdge(int u, int v) {
        checkVertex(u);
        checkVertex(v);

        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    private void checkVertex(int vertex) {
        Objects.checkIndex(vertex, adjacencyList.size());
    }
}
