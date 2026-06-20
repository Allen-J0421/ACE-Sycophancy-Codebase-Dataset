public final class WeightedGraph {

    public static final int DEFAULT_UNREACHABLE_DISTANCE = 100_000_000;

    private final int[][] adjacencyMatrix;
    private final int unreachableDistance;

    private WeightedGraph(int[][] adjacencyMatrix, int unreachableDistance) {
        validateSquareMatrix(adjacencyMatrix);
        validateUnreachableDistance(unreachableDistance);

        this.adjacencyMatrix = copyMatrix(adjacencyMatrix);
        this.unreachableDistance = unreachableDistance;
    }

    public static WeightedGraph from(int[][] adjacencyMatrix) {
        return new WeightedGraph(adjacencyMatrix, DEFAULT_UNREACHABLE_DISTANCE);
    }

    public static WeightedGraph from(int[][] adjacencyMatrix, int unreachableDistance) {
        return new WeightedGraph(adjacencyMatrix, unreachableDistance);
    }

    public int vertexCount() {
        return adjacencyMatrix.length;
    }

    public int unreachableDistance() {
        return unreachableDistance;
    }

    public int distance(int source, int target) {
        validateVertex(source, adjacencyMatrix.length);
        validateVertex(target, adjacencyMatrix.length);
        return adjacencyMatrix[source][target];
    }

    public boolean isUnreachable(int source, int target) {
        return distance(source, target) == unreachableDistance;
    }

    public int[][] adjacencyMatrix() {
        return copyMatrix(adjacencyMatrix);
    }

    private static void validateSquareMatrix(int[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Matrix must not be null.");
        }

        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] == null) {
                throw new IllegalArgumentException("Matrix row " + i + " must not be null.");
            }

            if (matrix[i].length != matrix.length) {
                throw new IllegalArgumentException("Matrix must be square.");
            }
        }
    }

    private static void validateUnreachableDistance(int unreachableDistance) {
        if (unreachableDistance <= 0) {
            throw new IllegalArgumentException("Unreachable distance must be positive.");
        }
    }

    private static void validateVertex(int vertex, int size) {
        if (vertex < 0 || vertex >= size) {
            throw new IllegalArgumentException("Vertex index out of bounds: " + vertex);
        }
    }

    private static int[][] copyMatrix(int[][] source) {
        int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();
        }
        return copy;
    }
}
