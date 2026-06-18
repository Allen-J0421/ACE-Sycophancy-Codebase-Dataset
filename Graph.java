import java.util.ArrayList;
import java.util.List;

public final class Graph implements IntGraph {
    private final int[][] adjacencyLists;

    private Graph(int[][] adjacencyLists) {
        this.adjacencyLists = adjacencyLists;
    }

    public static Builder builder(int vertexCount) {
        return new Builder(vertexCount);
    }

    @Override
    public int vertexCount() {
        return adjacencyLists.length;
    }

    @Override
    public int neighborCount(int vertex) {
        validateVertex(vertex);
        return adjacencyLists[vertex].length;
    }

    @Override
    public int neighborAt(int vertex, int neighborIndex) {
        validateVertex(vertex);
        if (neighborIndex < 0 || neighborIndex >= adjacencyLists[vertex].length) {
            throw new IllegalArgumentException("neighbor index out of bounds: " + neighborIndex);
        }

        return adjacencyLists[vertex][neighborIndex];
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyLists.length) {
            throw new IllegalArgumentException("vertex out of bounds: " + vertex);
        }
    }

    public static final class Builder {
        private final List<List<Integer>> adjacencyLists;

        private Builder(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must be non-negative");
            }

            this.adjacencyLists = new ArrayList<>(vertexCount);
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacencyLists.add(new ArrayList<>());
            }
        }

        public Builder addUndirectedEdge(int from, int to) {
            validateVertex(from);
            validateVertex(to);

            adjacencyLists.get(from).add(to);
            adjacencyLists.get(to).add(from);
            return this;
        }

        public Graph build() {
            int[][] packedAdjacencyLists = new int[adjacencyLists.size()][];
            for (int vertex = 0; vertex < adjacencyLists.size(); vertex++) {
                List<Integer> neighbors = adjacencyLists.get(vertex);
                packedAdjacencyLists[vertex] = new int[neighbors.size()];

                for (int index = 0; index < neighbors.size(); index++) {
                    packedAdjacencyLists[vertex][index] = neighbors.get(index);
                }
            }

            return new Graph(packedAdjacencyLists);
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= adjacencyLists.size()) {
                throw new IllegalArgumentException("vertex out of bounds: " + vertex);
            }
        }
    }
}
