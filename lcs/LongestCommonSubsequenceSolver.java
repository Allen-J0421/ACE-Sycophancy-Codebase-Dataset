package lcs;

import java.util.Objects;

final class LongestCommonSubsequenceSolver {
    private LongestCommonSubsequenceSolver() {
        // Utility class.
    }

    static int length(CharSequence first, CharSequence second) {
        Input input = Input.of(first, second);
        if (input.isEmpty()) {
            return 0;
        }

        return lengthOf(input.longer(), input.shorter());
    }

    static LcsResult analyze(CharSequence first, CharSequence second) {
        Input input = Input.of(first, second);
        if (input.isEmpty()) {
            return new LcsResult(0, "");
        }

        return analyzeWithTable(input.first(), input.second());
    }

    private static int lengthOf(CharSequence longer, CharSequence shorter) {
        int[] lengths = new int[shorter.length() + 1];

        for (int i = 1; i <= longer.length(); i++) {
            char longerChar = longer.charAt(i - 1);
            int previousDiagonal = 0;

            for (int j = 1; j <= shorter.length(); j++) {
                int saved = lengths[j];
                if (longerChar == shorter.charAt(j - 1)) {
                    lengths[j] = previousDiagonal + 1;
                } else {
                    lengths[j] = Math.max(lengths[j], lengths[j - 1]);
                }
                previousDiagonal = saved;
            }
        }

        return lengths[shorter.length()];
    }

    private static LcsResult analyzeWithTable(CharSequence first, CharSequence second) {
        int[][] lengths = new int[first.length() + 1][second.length() + 1];

        for (int i = 1; i <= first.length(); i++) {
            char firstChar = first.charAt(i - 1);
            for (int j = 1; j <= second.length(); j++) {
                if (firstChar == second.charAt(j - 1)) {
                    lengths[i][j] = lengths[i - 1][j - 1] + 1;
                } else {
                    lengths[i][j] = Math.max(lengths[i - 1][j], lengths[i][j - 1]);
                }
            }
        }

        StringBuilder subsequence = new StringBuilder(lengths[first.length()][second.length()]);
        int i = first.length();
        int j = second.length();
        while (i > 0 && j > 0) {
            if (first.charAt(i - 1) == second.charAt(j - 1)) {
                subsequence.append(first.charAt(i - 1));
                i--;
                j--;
            } else if (lengths[i - 1][j] >= lengths[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        return new LcsResult(lengths[first.length()][second.length()], subsequence.reverse().toString());
    }

    private record Input(CharSequence first, CharSequence second) {
        private Input {
            Objects.requireNonNull(first, "first");
            Objects.requireNonNull(second, "second");
        }

        static Input of(CharSequence first, CharSequence second) {
            return new Input(first, second);
        }

        boolean isEmpty() {
            return first.isEmpty() || second.isEmpty();
        }

        CharSequence longer() {
            return first.length() >= second.length() ? first : second;
        }

        CharSequence shorter() {
            return first.length() < second.length() ? first : second;
        }
    }
}
