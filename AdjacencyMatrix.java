import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class AdjacencyMatrix {
    private static final int NO_EDGE = 0;

    private final int[][] rows;

    private AdjacencyMatrix(int[][] rows) {
        this.rows = rows;
    }

    static AdjacencyMatrix fromRows(int[][] rows) {
        validate(rows);
        return new AdjacencyMatrix(copyRows(rows));
    }

    int vertexCount() {
        return rows.length;
    }

    List<Neighbor> neighborsOf(int vertex) {
        List<Neighbor> neighbors = new ArrayList<>();

        for (int candidateVertex = 0; candidateVertex < rows[vertex].length; candidateVertex++) {
            int edgeWeight = rows[vertex][candidateVertex];
            if (edgeWeight != NO_EDGE) {
                neighbors.add(new Neighbor(candidateVertex, edgeWeight));
            }
        }

        return List.copyOf(neighbors);
    }

    private static void validate(int[][] rows) {
        if (rows == null || rows.length == 0) {
            throw new IllegalArgumentException("Graph must contain at least one vertex.");
        }

        for (int row = 0; row < rows.length; row++) {
            if (rows[row] == null || rows[row].length != rows.length) {
                throw new IllegalArgumentException("Graph must be a square adjacency matrix.");
            }
        }

        for (int row = 0; row < rows.length; row++) {
            if (rows[row][row] != NO_EDGE) {
                throw new IllegalArgumentException("Graph diagonal must be zero.");
            }

            for (int column = row + 1; column < rows.length; column++) {
                if (rows[row][column] != rows[column][row]) {
                    throw new IllegalArgumentException("Graph must be undirected and symmetric.");
                }
            }
        }
    }

    private static int[][] copyRows(int[][] rows) {
        int[][] copiedRows = new int[rows.length][];

        for (int row = 0; row < rows.length; row++) {
            copiedRows[row] = Arrays.copyOf(rows[row], rows[row].length);
        }

        return copiedRows;
    }
}
