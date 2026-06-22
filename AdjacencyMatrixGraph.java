import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class AdjacencyMatrixGraph implements Graph {
    private static final int NO_EDGE = 0;

    private final List<List<Neighbor>> neighborsByVertex;

    private AdjacencyMatrixGraph(List<List<Neighbor>> neighborsByVertex) {
        this.neighborsByVertex = neighborsByVertex;
    }

    static AdjacencyMatrixGraph fromMatrix(int[][] adjacencyMatrix) {
        validateMatrix(adjacencyMatrix);
        return new AdjacencyMatrixGraph(buildNeighbors(adjacencyMatrix));
    }

    @Override
    public int vertexCount() {
        return neighborsByVertex.size();
    }

    @Override
    public List<Neighbor> neighborsOf(int vertex) {
        return neighborsByVertex.get(vertex);
    }

    private static void validateMatrix(int[][] adjacencyMatrix) {
        if (adjacencyMatrix == null || adjacencyMatrix.length == 0) {
            throw new IllegalArgumentException("Graph must contain at least one vertex.");
        }

        for (int row = 0; row < adjacencyMatrix.length; row++) {
            if (adjacencyMatrix[row] == null || adjacencyMatrix[row].length != adjacencyMatrix.length) {
                throw new IllegalArgumentException("Graph must be a square adjacency matrix.");
            }
        }

        for (int row = 0; row < adjacencyMatrix.length; row++) {
            if (adjacencyMatrix[row][row] != NO_EDGE) {
                throw new IllegalArgumentException("Graph diagonal must be zero.");
            }

            for (int column = row + 1; column < adjacencyMatrix.length; column++) {
                if (adjacencyMatrix[row][column] != adjacencyMatrix[column][row]) {
                    throw new IllegalArgumentException("Graph must be undirected and symmetric.");
                }
            }
        }
    }

    private static List<List<Neighbor>> buildNeighbors(int[][] adjacencyMatrix) {
        List<List<Neighbor>> neighborsByVertex = new ArrayList<>(adjacencyMatrix.length);

        for (int row = 0; row < adjacencyMatrix.length; row++) {
            int[] copiedRow = Arrays.copyOf(adjacencyMatrix[row], adjacencyMatrix[row].length);
            List<Neighbor> neighbors = new ArrayList<>();

            for (int column = 0; column < copiedRow.length; column++) {
                int edgeWeight = copiedRow[column];
                if (edgeWeight != NO_EDGE) {
                    neighbors.add(new Neighbor(column, edgeWeight));
                }
            }

            neighborsByVertex.add(List.copyOf(neighbors));
        }

        return List.copyOf(neighborsByVertex);
    }
}
