import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class DirectedGraph {
    private final int vertexCount;
    private final List<List<Integer>> adjacency;

    private DirectedGraph(int vertexCount, List<List<Integer>> adjacency) {
        this.vertexCount = vertexCount;
        this.adjacency = adjacency;
    }

    int vertexCount() {
        return vertexCount;
    }

    Iterable<Integer> neighbors(int vertex) {
        return adjacency.get(vertex);
    }

    static class Builder {
        private final int vertexCount;
        private final List<List<Integer>> adjacency;
        private boolean built = false;

        Builder(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("Vertex count must be non-negative: " + vertexCount);
            }
            this.vertexCount = vertexCount;
            this.adjacency = new ArrayList<>(vertexCount);
            for (int i = 0; i < vertexCount; i++) {
                adjacency.add(new ArrayList<>());
            }
        }

        Builder edge(int from, int to) {
            if (built) {
                throw new IllegalStateException("Cannot add edges after build() has been called");
            }
            checkVertex("from", from);
            checkVertex("to", to);
            adjacency.get(from).add(to);
            return this;
        }

        DirectedGraph build() {
            if (built) {
                throw new IllegalStateException("build() has already been called");
            }
            built = true;
            List<List<Integer>> sealed = new ArrayList<>(vertexCount);
            for (List<Integer> neighbors : adjacency) {
                sealed.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
            }
            return new DirectedGraph(vertexCount, Collections.unmodifiableList(sealed));
        }

        private void checkVertex(String label, int vertex) {
            if (vertex < 0 || vertex >= vertexCount) {
                throw new IllegalArgumentException(
                    label + " vertex " + vertex + " out of range [0, " + vertexCount + ")");
            }
        }
    }
}
