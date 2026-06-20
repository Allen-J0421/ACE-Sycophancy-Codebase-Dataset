public final class FlowNetwork {
    private final int[][] capacities;

    public FlowNetwork(int[][] capacities) {
        validate(capacities);
        this.capacities = IntMatrix.copyOf(capacities);
    }

    public int size() {
        return capacities.length;
    }

    public boolean contains(Vertex vertex) {
        return vertex != null && vertex.index() < capacities.length;
    }

    public int capacity(Vertex from, Vertex to) {
        validateVertex(from);
        validateVertex(to);
        return capacities[from.index()][to.index()];
    }

    public int[][] copyCapacities() {
        return IntMatrix.copyOf(capacities);
    }

    private void validateVertex(Vertex vertex) {
        if (!contains(vertex)) {
            throw new IllegalArgumentException("Vertex must belong to the network.");
        }
    }

    private static void validate(int[][] capacities) {
        if (capacities == null || capacities.length == 0) {
            throw new IllegalArgumentException("Graph must not be empty.");
        }

        int size = capacities.length;
        for (int row = 0; row < size; row++) {
            if (capacities[row] == null || capacities[row].length != size) {
                throw new IllegalArgumentException("Graph must be a square adjacency matrix.");
            }
            for (int column = 0; column < size; column++) {
                if (capacities[row][column] < 0) {
                    throw new IllegalArgumentException("Capacities must be non-negative.");
                }
            }
        }
    }
}
