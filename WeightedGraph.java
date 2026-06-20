public final class WeightedGraph {

    public static final int DEFAULT_UNREACHABLE_DISTANCE = 100_000_000;

    private final IntSquareMatrix adjacencyMatrix;
    private final int unreachableDistance;

    private WeightedGraph(int[][] adjacencyMatrix, int unreachableDistance) {
        this(IntSquareMatrix.from(adjacencyMatrix), unreachableDistance);
    }

    private WeightedGraph(IntSquareMatrix adjacencyMatrix, int unreachableDistance) {
        validateUnreachableDistance(unreachableDistance);

        this.adjacencyMatrix = adjacencyMatrix;
        this.unreachableDistance = unreachableDistance;
    }

    public static WeightedGraph from(int[][] adjacencyMatrix) {
        return new WeightedGraph(adjacencyMatrix, DEFAULT_UNREACHABLE_DISTANCE);
    }

    public static WeightedGraph from(int[][] adjacencyMatrix, int unreachableDistance) {
        return new WeightedGraph(adjacencyMatrix, unreachableDistance);
    }

    public int vertexCount() {
        return adjacencyMatrix.size();
    }

    public int unreachableDistance() {
        return unreachableDistance;
    }

    public int distance(int source, int target) {
        return adjacencyMatrix.get(source, target);
    }

    public boolean isUnreachable(int source, int target) {
        return distance(source, target) == unreachableDistance;
    }

    public IntSquareMatrix adjacencyMatrix() {
        return adjacencyMatrix;
    }

    private static void validateUnreachableDistance(int unreachableDistance) {
        if (unreachableDistance <= 0) {
            throw new IllegalArgumentException("Unreachable distance must be positive.");
        }
    }
}
