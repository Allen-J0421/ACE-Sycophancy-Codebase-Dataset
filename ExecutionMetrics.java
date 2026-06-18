/**
 * Tracks performance metrics during algorithm execution.
 *
 * Metrics tracked:
 * - Execution start/end time
 * - Number of vertices and edges processed
 * - Queue operations (enqueue, dequeue)
 * - Edge relaxations
 * - Total execution time
 *
 * Example:
 * {@code
 * ExecutionMetrics metrics = new ExecutionMetrics();
 * metrics.recordStart();
 * // ... algorithm execution ...
 * metrics.recordEnd();
 * System.out.println(metrics);  // Detailed statistics
 * }
 */
class ExecutionMetrics {
    private long startTime;
    private long endTime;
    private int verticesProcessed;
    private int edgesProcessed;
    private int enqueueCount;
    private int dequeueCount;
    private int relaxationCount;

    ExecutionMetrics() {
        this.startTime = 0;
        this.endTime = 0;
        this.verticesProcessed = 0;
        this.edgesProcessed = 0;
        this.enqueueCount = 0;
        this.dequeueCount = 0;
        this.relaxationCount = 0;
    }

    void recordStart() {
        this.startTime = System.nanoTime();
    }

    void recordEnd() {
        this.endTime = System.nanoTime();
    }

    void incrementVerticesProcessed() {
        this.verticesProcessed++;
    }

    void addEdgesProcessed(int count) {
        this.edgesProcessed += count;
    }

    void incrementEnqueueCount() {
        this.enqueueCount++;
    }

    void incrementDequeueCount() {
        this.dequeueCount++;
    }

    void incrementRelaxationCount() {
        this.relaxationCount++;
    }

    long getExecutionTimeMillis() {
        return (endTime - startTime) / 1_000_000;
    }

    long getExecutionTimeMicros() {
        return (endTime - startTime) / 1_000;
    }

    int getVerticesProcessed() {
        return verticesProcessed;
    }

    int getEdgesProcessed() {
        return edgesProcessed;
    }

    int getEnqueueCount() {
        return enqueueCount;
    }

    int getDequeueCount() {
        return dequeueCount;
    }

    int getRelaxationCount() {
        return relaxationCount;
    }

    @Override
    public String toString() {
        return String.format(
            "ExecutionMetrics {\n" +
            "  Time: %d ms (%d µs)\n" +
            "  Vertices processed: %d\n" +
            "  Edges processed: %d\n" +
            "  Queue enqueues: %d\n" +
            "  Queue dequeues: %d\n" +
            "  Edge relaxations: %d\n" +
            "}",
            getExecutionTimeMillis(), getExecutionTimeMicros(),
            verticesProcessed, edgesProcessed,
            enqueueCount, dequeueCount, relaxationCount
        );
    }
}
