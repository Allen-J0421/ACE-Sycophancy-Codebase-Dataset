import java.util.StringJoiner;

public final class TopologicalOrderFormatter {
    private TopologicalOrderFormatter() {
    }

    public static String format(TopologicalOrder order) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int vertex : order.vertices()) {
            joiner.add(String.valueOf(vertex));
        }
        return joiner.toString();
    }
}
