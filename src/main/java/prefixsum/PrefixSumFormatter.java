package prefixsum;

import java.util.List;
import java.util.StringJoiner;

final class PrefixSumFormatter {

    private PrefixSumFormatter() {
        // Utility class.
    }

    static String join(List<Integer> values) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int value : values) {
            joiner.add(Integer.toString(value));
        }
        return joiner.toString();
    }
}
