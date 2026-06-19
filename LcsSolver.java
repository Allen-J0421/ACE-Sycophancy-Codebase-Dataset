final class LcsSolver {
    private final char[] longer;
    private final char[] shorter;
    private final int[] lengths;

    LcsSolver(String first, String second) {
        char[] firstChars = first.toCharArray();
        char[] secondChars = second.toCharArray();

        if (firstChars.length >= secondChars.length) {
            this.longer = firstChars;
            this.shorter = secondChars;
        } else {
            this.longer = secondChars;
            this.shorter = firstChars;
        }

        this.lengths = new int[this.shorter.length + 1];
    }

    int solve() {
        for (char current : longer) {
            updateLengths(current);
        }

        return lengths[shorter.length];
    }

    private void updateLengths(char current) {
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
