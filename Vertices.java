/**
 * Validation helpers for vertex indices, shared by {@link Graph} and
 * {@link ShortestPaths} so the "valid vertex" rule lives in exactly one place.
 */
final class Vertices {

    private Vertices() {
        // Utility class; not instantiable.
    }

    /**
     * Ensures {@code vertex} is a legal index into a graph of {@code count}
     * vertices, i.e. in the range {@code [0, count - 1]}.
     *
     * @param vertex the index to check
     * @param count  the number of vertices in the graph
     * @param role   a human-readable label for the index (e.g. "source"),
     *               used to build a clear error message
     * @throws IndexOutOfBoundsException if {@code vertex} is out of range
     */
    static void requireValid(int vertex, int count, String role) {
        if (vertex < 0 || vertex >= count) {
            throw new IndexOutOfBoundsException(
                    role + " vertex " + vertex + " is out of range [0, " + (count - 1) + "]");
        }
    }
}
