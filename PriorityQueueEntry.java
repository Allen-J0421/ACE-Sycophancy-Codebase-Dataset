import java.util.Objects;

class PriorityQueueEntry implements Comparable<PriorityQueueEntry> {
    private final int distance;
    private final int node;

    private PriorityQueueEntry(int distance, int node) {
        if (distance < 0) {
            throw new IllegalArgumentException("Distance must be non-negative");
        }
        this.distance = distance;
        this.node = node;
    }

    static PriorityQueueEntry of(int distance, int node) {
        return new PriorityQueueEntry(distance, node);
    }

    int getDistance() {
        return distance;
    }

    int getNode() {
        return node;
    }

    @Override
    public int compareTo(PriorityQueueEntry other) {
        Objects.requireNonNull(other);
        return Integer.compare(this.distance, other.distance);
    }

    @Override
    public String toString() {
        return String.format("Entry(distance=%d, node=%d)", distance, node);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PriorityQueueEntry)) return false;
        PriorityQueueEntry other = (PriorityQueueEntry) obj;
        return distance == other.distance && node == other.node;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance, node);
    }
}
