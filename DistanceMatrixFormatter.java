public final class DistanceMatrixFormatter {
    private DistanceMatrixFormatter() {
    }

    public static String format(int[][] distances) {
        StringBuilder output = new StringBuilder();
        for (int[] row : distances) {
            output.append(formatRow(row)).append(System.lineSeparator());
        }
        return output.toString();
    }

    private static String formatRow(int[] row) {
        StringBuilder output = new StringBuilder();
        for (int column = 0; column < row.length; column++) {
            if (column > 0) {
                output.append(' ');
            }
            output.append(formatDistance(row[column]));
        }
        return output.toString();
    }

    private static String formatDistance(int distance) {
        if (FloydWarshall.isReachable(distance)) {
            return Integer.toString(distance);
        }
        return "INF";
    }
}
