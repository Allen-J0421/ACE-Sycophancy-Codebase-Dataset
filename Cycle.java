import java.util.List;

/**
 * A directed cycle, represented as a closed walk whose first and last vertices
 * are the same — for example {@code [0, 1, 2, 0]} for the cycle 0 → 1 → 2 → 0.
 * A self-loop on vertex {@code v} is represented as {@code [v, v]}.
 *
 * <p>The vertex list is defensively copied and exposed unmodifiable.
 */
record Cycle(List<Integer> vertices) {

    Cycle {
        vertices = List.copyOf(vertices);
    }

    /** Renders the cycle as {@code 0 -> 1 -> 2 -> 0}. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vertices.size(); i++) {
            if (i > 0) {
                sb.append(" -> ");
            }
            sb.append(vertices.get(i));
        }
        return sb.toString();
    }
}
