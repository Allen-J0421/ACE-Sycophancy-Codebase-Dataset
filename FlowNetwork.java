public final class FlowNetwork {
    private final int[][] capacities;

    public FlowNetwork(int[][] capacities) {
        validate(capacities);
        this.capacities = copyOf(capacities);
    }

    public int size() {
        return capacities.length;
    }

    public int capacity(int from, int to) {
        return capacities[from][to];
    }

    public int[][] copyCapacities() {
        return copyOf(capacities);
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

    private static int[][] copyOf(int[][] source) {
        int[][] copy = new int[source.length][source.length];
        for (int row = 0; row < source.length; row++) {
            System.arraycopy(source[row], 0, copy[row], 0, source.length);
        }
        return copy;
    }
}
