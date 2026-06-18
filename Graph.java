import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Graph {
    private final int vertexCount;
    private final List<List<Integer>> adjacencyList;

    private Graph(int vertexCount, List<List<Integer>> adjacencyList) {
        this.vertexCount = vertexCount;
        this.adjacencyList = adjacencyList;
    }

    int vertexCount() {
        return vertexCount;
    }

    List<Integer> neighbors(int vertex) {
        validateVertex(vertex);
        return adjacencyList.get(vertex);
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                "Vertex " + vertex + " is out of range [0, " + (vertexCount - 1) + "]");
        }
    }

    static final class Builder {
        private final int vertexCount;
        private final List<List<Integer>> adjacencyList;

        Builder(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must be non-negative, got " + vertexCount);
            }
            this.vertexCount = vertexCount;
            adjacencyList = new ArrayList<>(vertexCount);
            for (int i = 0; i < vertexCount; i++) {
                adjacencyList.add(new ArrayList<>());
            }
        }

        Builder addEdge(int u, int v) {
            validateVertex(u);
            validateVertex(v);
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
            return this;
        }

        Graph build() {
            List<List<Integer>> frozen = new ArrayList<>(vertexCount);
            for (List<Integer> neighbors : adjacencyList) {
                frozen.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
            }
            return new Graph(vertexCount, Collections.unmodifiableList(frozen));
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= vertexCount) {
                throw new IllegalArgumentException(
                    "Vertex " + vertex + " is out of range [0, " + (vertexCount - 1) + "]");
            }
        }
    }
}
