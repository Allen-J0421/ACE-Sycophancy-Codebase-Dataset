public final class DistanceMatrixFormatter {
    private DistanceMatrixFormatter() {
    }

    public static String format(int[][] distances) {
        Matrices.requireRectangular(distances, "Distance matrix");

        StringBuilder output = new StringBuilder();
        for (int[] row : distances) {
            output.append(formatRow(row)).append(System.lineSeparator());
        }
        return output.toString();
    }

    private static String formatRow(int[] row) {
        StringBuilder output = new StringBuilder();

        for (int index = 0; index < row.length; index++) {
            if (index > 0) {
                output.append(' ');
            }
            output.append(formatDistance(row[index]));
        }

        return output.toString();
    }

    private static String formatDistance(int distance) {
        return FloydWarshall.isReachable(distance) ? Integer.toString(distance) : "INF";
    }
}
