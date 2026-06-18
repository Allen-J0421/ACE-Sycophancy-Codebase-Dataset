final class QueueEntry {
    private final int vertex;
    private final int distance;

    QueueEntry(int vertex, int distance) {
        this.vertex = vertex;
        this.distance = distance;
    }

    int vertex() {
        return vertex;
    }

    int distance() {
        return distance;
    }
}
