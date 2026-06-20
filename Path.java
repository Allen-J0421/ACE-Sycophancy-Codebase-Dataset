import java.util.List;

/**
 * An immutable shortest path: the sequence of vertices from a source to a target,
 * together with the total weight of traversing it.
 *
 * <p>When no route exists between two vertices, {@link #none()} is used: it has an
 * empty vertex sequence, reported by {@link #isEmpty()}.
 */
record Path(List<Integer> vertices, int totalWeight) {

    private static final Path NONE = new Path(List.of(), 0);

    /** Defensive, immutable copy so the path cannot be mutated after construction. */
    Path {
        vertices = List.copyOf(vertices);
    }

    static Path of(List<Integer> vertices, int totalWeight) {
        return new Path(vertices, totalWeight);
    }

    /** The absence of a path, e.g. to an unreachable vertex. */
    static Path none() {
        return NONE;
    }

    /** Whether this represents "no path" (an empty vertex sequence). */
    boolean isEmpty() {
        return vertices.isEmpty();
    }

    /** The number of edges traversed; {@code 0} for an empty path or a single vertex. */
    int edgeCount() {
        return Math.max(0, vertices.size() - 1);
    }

    @Override
    public String toString() {
        return isEmpty() ? "Path[none]" : "Path" + vertices + " (weight " + totalWeight + ")";
    }
}
