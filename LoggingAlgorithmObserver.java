import java.util.Arrays;

/**
 * Observer that logs all algorithm execution events.
 *
 * Provides detailed progress tracking and final statistics.
 * Useful for debugging, performance analysis, and understanding
 * algorithm behavior.
 *
 * Example output:
 * {@code
 * [AlgorithmObserver] Starting Dijkstra from node 0 (5 vertices)
 * [AlgorithmObserver] Vertex 0 processed with distance 0
 * [AlgorithmObserver] Edge relaxed: 0→1 (0+4=4)
 * ...
 * [AlgorithmObserver] Algorithm complete in 2 ms
 * [AlgorithmObserver] Final distances: [0, 4, 7, 9, 10]
 * [AlgorithmObserver] Total relaxations: 5
 * }
 */
class LoggingAlgorithmObserver implements AlgorithmObserver {
    private static final String PREFIX = "[AlgorithmObserver]";
    private int vertexCount = 0;
    private int relaxationCount = 0;

    @Override
    public void onAlgorithmStart(String algorithmName, int sourceNode, int vertexCount) {
        this.vertexCount = vertexCount;
        System.out.println(PREFIX + " Starting " + algorithmName +
                         " from node " + sourceNode +
                         " (" + vertexCount + " vertices)");
    }

    @Override
    public void onVertexProcessed(int vertex, int distance) {
        System.out.println(PREFIX + " Vertex " + vertex +
                         " processed with distance " + distance);
    }

    @Override
    public void onEdgeRelaxed(int from, int to, int oldDistance, int newDistance, int relaxationCount) {
        this.relaxationCount = relaxationCount;
        System.out.println(PREFIX + " Edge relaxed: " + from + "→" + to +
                         " (" + oldDistance + "+" + (newDistance - oldDistance) +
                         "=" + newDistance + ")");
    }

    @Override
    public void onAlgorithmComplete(int[] finalDistances, int totalRelaxations, long executionTimeMillis) {
        System.out.println(PREFIX + " Algorithm complete in " + executionTimeMillis + " ms");
        System.out.println(PREFIX + " Final distances: " + Arrays.toString(finalDistances));
        System.out.println(PREFIX + " Total relaxations: " + totalRelaxations);
        System.out.println(PREFIX + " Vertices processed: " + vertexCount);
    }

    @Override
    public String toString() {
        return PREFIX + " (relaxations observed: " + relaxationCount + ")";
    }
}
