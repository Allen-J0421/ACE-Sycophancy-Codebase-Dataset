package lcs;

final class LongestCommonSubsequenceSolver {
    private LongestCommonSubsequenceSolver() {
        // Utility class.
    }

    static int length(CharSequence first, CharSequence second) {
        LcsInput input = LcsInput.of(first, second);
        if (input.isEmpty()) {
            return 0;
        }

        return lengthOf(input.longer(), input.shorter());
    }

    static LcsResult analyze(CharSequence first, CharSequence second) {
        LcsInput input = LcsInput.of(first, second);
        if (input.isEmpty()) {
            return new LcsResult(0, "");
        }

        return LcsTable.build(input.first(), input.second()).toResult();
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
}
