final class LcsSolver {
    private final char[] longer;
    private final char[] shorter;
    private final int[] lengths;

    LcsSolver(LcsRequest request) {
        this.longer = request.longer();
        this.shorter = request.shorter();
        this.lengths = new int[shorter.length + 1];
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
