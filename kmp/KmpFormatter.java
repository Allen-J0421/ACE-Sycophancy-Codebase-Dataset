package kmp;

import java.util.List;

public final class KmpFormatter {

    private KmpFormatter() {
        // Utility class.
    }

    public static String joinMatches(List<Integer> matches) {
        if (matches.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < matches.size(); i++) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(matches.get(i));
        }
        return builder.toString();
    }
}
