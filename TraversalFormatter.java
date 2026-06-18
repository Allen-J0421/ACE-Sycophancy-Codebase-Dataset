import java.util.List;
import java.util.Objects;

final class TraversalFormatter {
    private TraversalFormatter() {
    }

    static String format(List<Integer> traversal) {
        Objects.requireNonNull(traversal, "traversal must not be null");

        StringBuilder output = new StringBuilder();
        for (int vertex : traversal) {
            output.append(vertex).append(' ');
        }
        return output.toString();
    }
}
