import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DirectedGraph {
    private final List<List<Integer>> adjacencyList;

    private DirectedGraph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public static Builder builder(int vertexCount) {
        return new Builder(vertexCount);
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return adjacencyList.get(vertex);
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount()) {
            throw new IndexOutOfBoundsException("vertex out of range: " + vertex);
        }
    }

    public static final class Builder {
        private final List<List<Integer>> adjacencyList;

        private Builder(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must be non-negative");
            }

            adjacencyList = new ArrayList<>(vertexCount);
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacencyList.add(new ArrayList<>());
            }
        }

        public Builder addEdge(int from, int to) {
            validateVertex(from);
            validateVertex(to);
            adjacencyList.get(from).add(to);
            return this;
        }

        public DirectedGraph build() {
            List<List<Integer>> immutableAdjacencyList = new ArrayList<>(adjacencyList.size());
            for (List<Integer> neighbors : adjacencyList) {
                immutableAdjacencyList.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
            }
            return new DirectedGraph(Collections.unmodifiableList(immutableAdjacencyList));
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= adjacencyList.size()) {
                throw new IndexOutOfBoundsException("vertex out of range: " + vertex);
            }
        }
    }
}
