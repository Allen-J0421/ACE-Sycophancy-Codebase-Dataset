import java.util.ArrayList;
import java.util.List;

public final class DirectedGraphBuilder {
    private final List<List<Integer>> adjacencyList;

    public DirectedGraphBuilder(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }

        adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public DirectedGraphBuilder addEdge(int from, int to) {
        validateVertex(from);
        validateVertex(to);
        adjacencyList.get(from).add(to);
        return this;
    }

    public DirectedGraph build() {
        return new DirectedGraph(adjacencyList);
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IndexOutOfBoundsException("vertex out of range: " + vertex);
        }
    }
}
