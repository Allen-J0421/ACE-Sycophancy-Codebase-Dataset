final class IntMatrix {
    private IntMatrix() {
    }

    static int[][] copyOf(int[][] source) {
        int[][] copy = new int[source.length][source.length];
        for (int row = 0; row < source.length; row++) {
            System.arraycopy(source[row], 0, copy[row], 0, source.length);
        }
        return copy;
    }
}
