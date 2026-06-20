import java.util.ArrayDeque;
import java.util.Arrays;

final class FordFulkersonSolver {
    private final int[][] capacity;
    private final int vertexCount;
    private final int source;
    private final int sink;

    FordFulkersonSolver(int[][] capacity, int source, int sink) {
        validateCapacityMatrix(capacity);

        this.vertexCount = capacity.length;
        this.source = validateVertex(source, vertexCount, "source");
        this.sink = validateVertex(sink, vertexCount, "sink");

        if (source == sink) {
            throw new IllegalArgumentException("source and sink must be different vertices");
        }

        this.capacity = copyMatrix(capacity);
    }

    int computeMaxFlow() {
        int[][] residualGraph = copyMatrix(capacity);
        int[] parent = new int[vertexCount];
        int maxFlow = 0;

        while (findAugmentingPath(residualGraph, parent)) {
            int pathFlow = findBottleneckCapacity(residualGraph, parent);
            applyAugmentation(residualGraph, parent, pathFlow);
            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    private boolean findAugmentingPath(int[][] residualGraph, int[] parent) {
        boolean[] visited = new boolean[vertexCount];
        Arrays.fill(parent, -1);

        ArrayDeque<Integer> queue = new ArrayDeque<>();
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int currentVertex = queue.removeFirst();

            for (int nextVertex = 0; nextVertex < vertexCount; nextVertex++) {
                if (!visited[nextVertex] && residualGraph[currentVertex][nextVertex] > 0) {
                    parent[nextVertex] = currentVertex;
                    if (nextVertex == sink) {
                        return true;
                    }

                    visited[nextVertex] = true;
                    queue.addLast(nextVertex);
                }
            }
        }

        return false;
    }

    private int findBottleneckCapacity(int[][] residualGraph, int[] parent) {
        int pathFlow = Integer.MAX_VALUE;

        for (int vertex = sink; vertex != source; vertex = parent[vertex]) {
            int predecessor = parent[vertex];
            pathFlow = Math.min(pathFlow, residualGraph[predecessor][vertex]);
        }

        return pathFlow;
    }

    private void applyAugmentation(int[][] residualGraph, int[] parent, int pathFlow) {
        for (int vertex = sink; vertex != source; vertex = parent[vertex]) {
            int predecessor = parent[vertex];
            residualGraph[predecessor][vertex] -= pathFlow;
            residualGraph[vertex][predecessor] += pathFlow;
        }
    }

    private static void validateCapacityMatrix(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException("capacity matrix must not be null or empty");
        }

        if (matrix[0] == null) {
            throw new IllegalArgumentException("capacity matrix rows must not be null");
        }

        int expectedWidth = matrix[0].length;
        if (expectedWidth == 0) {
            throw new IllegalArgumentException("capacity matrix must contain at least one vertex");
        }

        for (int row = 0; row < matrix.length; row++) {
            if (matrix[row] == null || matrix[row].length != expectedWidth) {
                throw new IllegalArgumentException("capacity matrix must be rectangular");
            }
            if (matrix[row].length != matrix.length) {
                throw new IllegalArgumentException("capacity matrix must be square");
            }
        }
    }

    private static int validateVertex(int vertex, int vertexCount, String label) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                label + " vertex must be between 0 and " + (vertexCount - 1));
        }

        return vertex;
    }

    private static int[][] copyMatrix(int[][] sourceMatrix) {
        int[][] copy = new int[sourceMatrix.length][sourceMatrix.length];
        for (int row = 0; row < sourceMatrix.length; row++) {
            System.arraycopy(sourceMatrix[row], 0, copy[row], 0, sourceMatrix[row].length);
        }
        return copy;
    }
}

final class FordFulkersonExample {
    private FordFulkersonExample() {
        // Utility class.
    }

    public static void main(String[] args) {
        int[][] graph = {
            { 0, 16, 13, 0, 0, 0 },
            { 0, 0, 10, 12, 0, 0 },
            { 0, 4, 0, 0, 14, 0 },
            { 0, 0, 9, 0, 0, 20 },
            { 0, 0, 0, 7, 0, 4 },
            { 0, 0, 0, 0, 0, 0 }
        };

        FordFulkersonSolver solver = new FordFulkersonSolver(graph, 0, 5);
        System.out.println("The maximum possible flow is " + solver.computeMaxFlow());
    }
}
