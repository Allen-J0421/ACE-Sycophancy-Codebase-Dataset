import java.util.List;

/**
 * The result when a negative-weight cycle is reachable from the source.
 *
 * <p>No shortest path is well defined in this case, so the type carries no
 * distances. It does, however, report the {@linkplain #vertices() vertices of the
 * offending cycle}, listed in traversal order: for consecutive entries
 * {@code a, b} the edge {@code a -> b} exists, and the final vertex closes back to
 * the first.
 */
record NegativeCycle(List<Integer> vertices) implements ShortestPathResult {

    /** Defensive, immutable copy so the reported cycle cannot be mutated. */
    NegativeCycle {
        vertices = List.copyOf(vertices);
    }

    @Override
    public String toString() {
        return "NegativeCycle" + vertices;
    }
}
