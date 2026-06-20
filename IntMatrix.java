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

    static boolean equals(int[][] left, int[][] right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null || left.length != right.length) {
            return false;
        }

        for (int row = 0; row < left.length; row++) {
            if (left[row].length != right[row].length) {
                return false;
            }
            for (int column = 0; column < left[row].length; column++) {
                if (left[row][column] != right[row][column]) {
                    return false;
                }
            }
        }

        return true;
    }

    static int hashCode(int[][] matrix) {
        int result = 1;

        for (int[] row : matrix) {
            for (int value : row) {
                result = 31 * result + value;
            }
        }

        return result;
    }

    static String toDisplayString(int[][] matrix) {
        StringBuilder builder = new StringBuilder("[");

        for (int row = 0; row < matrix.length; row++) {
            if (row > 0) {
                builder.append(", ");
            }
            builder.append('[');
            for (int column = 0; column < matrix[row].length; column++) {
                if (column > 0) {
                    builder.append(", ");
                }
                builder.append(matrix[row][column]);
            }
            builder.append(']');
        }

        return builder.append(']').toString();
    }
}
