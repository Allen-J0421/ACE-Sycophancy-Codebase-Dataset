final class CapacityMatrixValidator {
    private CapacityMatrixValidator() {
    }

    static void validate(int[][] graph, int source, int sink) {
        if (graph == null || graph.length == 0) {
            throw new IllegalArgumentException("Graph must contain at least one vertex.");
        }

        if (!isValidVertex(source, graph.length) || !isValidVertex(sink, graph.length)) {
            throw new IllegalArgumentException("Source and sink must be valid vertex indexes.");
        }

        if (source == sink) {
            throw new IllegalArgumentException("Source and sink must be different vertices.");
        }

        for (int row = 0; row < graph.length; row++) {
            validateRow(graph[row], graph.length);
        }
    }

    private static void validateRow(int[] row, int expectedLength) {
        if (row == null || row.length != expectedLength) {
            throw new IllegalArgumentException("Graph must be a square capacity matrix.");
        }

        for (int capacity : row) {
            if (capacity < 0) {
                throw new IllegalArgumentException("Graph capacities must be non-negative.");
            }
        }
    }

    private static boolean isValidVertex(int vertex, int vertexCount) {
        return vertex >= 0 && vertex < vertexCount;
    }
}
