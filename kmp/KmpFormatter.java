package kmp;

import java.util.List;
import java.util.StringJoiner;

final class KmpFormatter {

    private KmpFormatter() {
        // Utility class.
    }

    static String joinMatches(List<Integer> matches) {
        if (matches.isEmpty()) {
            return "";
        }

        StringJoiner joiner = new StringJoiner(" ");
        for (Integer match : matches) {
            joiner.add(String.valueOf(match));
        }
        return joiner.toString();
    }
}
