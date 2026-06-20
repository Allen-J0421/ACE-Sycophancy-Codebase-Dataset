final class MatrixValidator {
    private static final String NULL_SUFFIX = " must not be null.";
    private static final String NULL_ROW_SUFFIX = " must not contain null rows.";
    private static final String RECTANGULAR_SUFFIX = " must be rectangular.";
    private static final String SQUARE_SUFFIX = " must be square.";

    private MatrixValidator() {
    }

    static int requireSquare(int[][] matrix, String label) {
        int columnCount = requireRectangular(matrix, label);
        if (matrix.length != columnCount) {
            throw new IllegalArgumentException(label + SQUARE_SUFFIX);
        }
        return matrix.length;
    }

    static int requireRectangular(int[][] matrix, String label) {
        requireMatrix(matrix, label);

        if (matrix.length == 0) {
            return 0;
        }

        int columnCount = requireRow(matrix[0], label).length;
        for (int rowIndex = 1; rowIndex < matrix.length; rowIndex++) {
            int[] row = requireRow(matrix[rowIndex], label);
            if (row.length != columnCount) {
                throw new IllegalArgumentException(label + RECTANGULAR_SUFFIX);
            }
        }

        return columnCount;
    }

    private static void requireMatrix(int[][] matrix, String label) {
        if (matrix == null) {
            throw new IllegalArgumentException(label + NULL_SUFFIX);
        }
    }

    private static int[] requireRow(int[] row, String label) {
        if (row == null) {
            throw new IllegalArgumentException(label + NULL_ROW_SUFFIX);
        }
        return row;
    }
}
