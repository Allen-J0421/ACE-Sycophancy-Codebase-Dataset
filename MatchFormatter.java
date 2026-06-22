import java.util.List;
import java.util.Objects;

final class MatchFormatter {
    private MatchFormatter() {
    }

    static String format(List<Integer> matches) {
        Objects.requireNonNull(matches, "matches");

        StringBuilder output = new StringBuilder();

        for (int match : matches) {
            output.append(match).append(' ');
        }

        return output.toString();
    }
}
