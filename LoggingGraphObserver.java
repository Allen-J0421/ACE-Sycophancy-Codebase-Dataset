/**
 * Observer that logs all graph modification events.
 *
 * Useful for debugging, auditing, and understanding
 * graph construction in detail.
 *
 * Example output:
 * {@code
 * [GraphObserver] Graph created with 5 vertices
 * [GraphObserver] Edge added: 0 → 1 (weight=4)
 * [GraphObserver] Edge added: 0 → 2 (weight=8)
 * [GraphObserver] Edge added: 1 → 2 (weight=3)
 * }
 */
class LoggingGraphObserver implements GraphObserver {
    private static final String PREFIX = "[GraphObserver]";
    private int edgeCount = 0;

    @Override
    public void onGraphCreated(int vertexCount) {
        System.out.println(PREFIX + " Graph created with " + vertexCount + " vertices");
    }

    @Override
    public void onEdgeAdded(int source, int destination, int weight) {
        edgeCount++;
        System.out.println(PREFIX + " Edge #" + edgeCount + " added: " +
                         source + " → " + destination + " (weight=" + weight + ")");
    }

    /**
     * Gets total number of edges observed.
     */
    int getEdgeCount() {
        return edgeCount;
    }

    @Override
    public String toString() {
        return PREFIX + " (edges observed: " + edgeCount + ")";
    }
}
