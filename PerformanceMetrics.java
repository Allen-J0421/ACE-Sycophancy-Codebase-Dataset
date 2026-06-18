public class PerformanceMetrics {
    private final long startTime;
    private long endTime;
    private final long memoryStart;
    private long memoryEnd;
    private int verticesVisited;
    private int edgesTraversed;

    public PerformanceMetrics() {
        this.startTime = System.nanoTime();
        this.memoryStart = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        this.verticesVisited = 0;
        this.edgesTraversed = 0;
    }

    public void recordVertexVisit() {
        verticesVisited++;
    }

    public void recordEdgeTraversal() {
        edgesTraversed++;
    }

    public void stop() {
        this.endTime = System.nanoTime();
        this.memoryEnd = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public long getDurationNanos() {
        return endTime - startTime;
    }

    public long getDurationMillis() {
        return getDurationNanos() / 1_000_000;
    }

    public double getDurationSeconds() {
        return getDurationNanos() / 1_000_000_000.0;
    }

    public long getMemoryUsedBytes() {
        return memoryEnd - memoryStart;
    }

    public double getMemoryUsedMB() {
        return getMemoryUsedBytes() / (1024.0 * 1024.0);
    }

    public int getVerticesVisited() {
        return verticesVisited;
    }

    public int getEdgesTraversed() {
        return edgesTraversed;
    }

    public double getVerticesPerSecond() {
        double seconds = getDurationSeconds();
        return seconds > 0 ? verticesVisited / seconds : 0;
    }

    public double getEdgesPerSecond() {
        double seconds = getDurationSeconds();
        return seconds > 0 ? edgesTraversed / seconds : 0;
    }

    public void print() {
        System.out.println("=== Performance Metrics ===");
        System.out.printf("Duration: %.3f ms%n", (double) getDurationMillis());
        System.out.printf("Memory Used: %.2f MB%n", getMemoryUsedMB());
        System.out.println("Vertices Visited: " + verticesVisited);
        System.out.println("Edges Traversed: " + edgesTraversed);
        System.out.printf("Throughput: %.0f vertices/sec, %.0f edges/sec%n",
            getVerticesPerSecond(), getEdgesPerSecond());
    }
}
