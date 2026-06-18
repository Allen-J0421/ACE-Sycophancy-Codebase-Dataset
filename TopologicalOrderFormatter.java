import java.util.List;
import java.util.StringJoiner;

public final class TopologicalOrderFormatter {
    private TopologicalOrderFormatter() {
    }

    public static String format(List<Integer> order) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int vertex : order) {
            joiner.add(String.valueOf(vertex));
        }
        return joiner.toString();
    }
}
