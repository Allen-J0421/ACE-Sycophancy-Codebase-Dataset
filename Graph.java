import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Graph {

    private final List<List<Integer>> adjacencyList;

    private Graph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public static Graph fromUndirectedEdges(int vertices, int[][] edges) {
        Objects.requireNonNull(edges, "edges");

        Builder builder = builder(vertices);
        for (int[] edge : edges) {
            if (edge == null || edge.length != 2) {
                throw new IllegalArgumentException(
                        "Each edge must contain exactly two vertices: " + Arrays.toString(edge));
            }
            builder.addUndirectedEdge(edge[0], edge[1]);
        }

        return builder.build();
    }

    public static Builder builder(int vertices) {
        return new Builder(vertices);
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public List<Integer> neighbors(int vertex) {
        checkVertex(vertex);
        return adjacencyList.get(vertex);
    }

    private void checkVertex(int vertex) {
        Objects.checkIndex(vertex, adjacencyList.size());
    }

    public static final class Builder {

        private final List<List<Integer>> adjacencyList;
        private boolean built;

        private Builder(int vertices) {
            if (vertices < 0) {
                throw new IllegalArgumentException("vertices must be non-negative");
            }

            adjacencyList = new ArrayList<>(vertices);
            for (int vertex = 0; vertex < vertices; vertex++) {
                adjacencyList.add(new ArrayList<>());
            }
        }

        public Builder addUndirectedEdge(int u, int v) {
            ensureNotBuilt();
            checkVertex(u);
            checkVertex(v);

            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
            return this;
        }

        public Graph build() {
            ensureNotBuilt();
            built = true;

            List<List<Integer>> frozenAdjacencyList = new ArrayList<>(adjacencyList.size());
            for (List<Integer> neighbors : adjacencyList) {
                frozenAdjacencyList.add(List.copyOf(neighbors));
            }
            return new Graph(List.copyOf(frozenAdjacencyList));
        }

        private void ensureNotBuilt() {
            if (built) {
                throw new IllegalStateException("Builder has already built a graph");
            }
        }

        private void checkVertex(int vertex) {
            Objects.checkIndex(vertex, adjacencyList.size());
        }
    }
}
