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
        private final IntRowBuilder[] adjacencyLists;

        private Builder(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must be non-negative");
            }

            this.adjacencyLists = new IntRowBuilder[vertexCount];
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacencyLists[vertex] = new IntRowBuilder();
            }
        }

        public Builder addDirectedEdge(int from, int to) {
            validateVertex(from);
            validateVertex(to);
            adjacencyLists[from].add(to);
            return this;
        }

        public Builder addUndirectedEdge(int from, int to) {
            return addDirectedEdge(from, to).addDirectedEdge(to, from);
        }

        public Graph build() {
            int[][] packedAdjacencyLists = new int[adjacencyLists.length][];
            for (int vertex = 0; vertex < adjacencyLists.length; vertex++) {
                packedAdjacencyLists[vertex] = adjacencyLists[vertex].toArray();
            }

            return new Graph(packedAdjacencyLists);
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= adjacencyLists.length) {
                throw new IllegalArgumentException("vertex out of bounds: " + vertex);
            }
        }
    }

    private static final class IntRowBuilder {
        private static final int DEFAULT_CAPACITY = 4;

        private int[] values = new int[DEFAULT_CAPACITY];
        private int size;

        private void add(int value) {
            ensureCapacity(size + 1);
            values[size] = value;
            size++;
        }

        private int[] toArray() {
            int[] copy = new int[size];
            System.arraycopy(values, 0, copy, 0, size);
            return copy;
        }

        private void ensureCapacity(int minCapacity) {
            if (minCapacity <= values.length) {
                return;
            }

            int newCapacity = Math.max(minCapacity, values.length * 2);
            int[] expanded = new int[newCapacity];
            System.arraycopy(values, 0, expanded, 0, size);
            values = expanded;
        }
    }
}
