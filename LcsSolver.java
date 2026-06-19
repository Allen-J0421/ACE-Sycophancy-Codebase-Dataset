final class LcsSolver {
    private LcsSolver() {
    }

    static int solve(NormalizedSequences sequences) {
        char[] longer = sequences.longer();
        char[] shorter = sequences.shorter();
        int[] lengths = new int[shorter.length + 1];

        for (char current : longer) {
            updateLengths(current, shorter, lengths);
        }

        return lengths[shorter.length];
    }

    private static void updateLengths(char current, char[] shorter, int[] lengths) {
        int diagonal = 0;

        for (int j = 1; j <= shorter.length; j++) {
            int previousRow = lengths[j];

            lengths[j] = current == shorter[j - 1]
                ? diagonal + 1
                : Math.max(lengths[j], lengths[j - 1]);
            diagonal = previousRow;
        }
    }
}
