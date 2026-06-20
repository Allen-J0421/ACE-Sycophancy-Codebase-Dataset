import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class FloydWarshall {

    private static final int INF = 100_000_000;

    private FloydWarshall() {
        // Utility class.
    }

    static Result computeShortestPaths(int[][] graph) {
        validateSquareMatrix(graph);

        int[][] distances = copyMatrix(graph);
        int[][] nextHop = new int[graph.length][graph.length];
        initializeNextHop(graph, nextHop);

        int vertices = distances.length;
        for (int k = 0; k < vertices; k++) {
            for (int i = 0; i < vertices; i++) {
                if (distances[i][k] == INF) {
                    continue;
                }

                for (int j = 0; j < vertices; j++) {
                    if (distances[k][j] == INF) {
                        continue;
                    }

                    int throughK = distances[i][k] + distances[k][j];
                    if (throughK < distances[i][j]) {
                        distances[i][j] = throughK;
                        nextHop[i][j] = nextHop[i][k];
                    }
                }
            }
        }

        return new Result(distances, nextHop);
    }

    private static void initializeNextHop(int[][] graph, int[][] nextHop) {
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph.length; j++) {
                nextHop[i][j] = graph[i][j] == INF ? -1 : j;
            }
        }
    }

    private static void validateSquareMatrix(int[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Matrix must not be null.");
        }

        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] == null) {
                throw new IllegalArgumentException("Matrix row " + i + " must not be null.");
            }

            if (matrix[i].length != matrix.length) {
                throw new IllegalArgumentException("Matrix must be square.");
            }
        }
    }

    private static void validateVertex(int vertex, int size) {
        if (vertex < 0 || vertex >= size) {
            throw new IllegalArgumentException("Vertex index out of bounds: " + vertex);
        }
    }

    private static int[][] copyMatrix(int[][] source) {
        int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();
        }
        return copy;
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            StringBuilder line = new StringBuilder();
            for (int value : row) {
                if (line.length() > 0) {
                    line.append(' ');
                }
                line.append(value);
            }
            System.out.println(line);
        }
    }

    private static int[][] sampleGraph() {
        return new int[][] {
            {0, 4, INF, 5, INF},
            {INF, 0, 1, INF, 6},
            {2, INF, 0, 3, INF},
            {INF, INF, 1, 0, 2},
            {1, INF, INF, 4, 0}
        };
    }

    static final class Result {
        private final int[][] distances;
        private final int[][] nextHop;

        private Result(int[][] distances, int[][] nextHop) {
            this.distances = copyMatrix(distances);
            this.nextHop = copyMatrix(nextHop);
        }

        int[][] distances() {
            return copyMatrix(distances);
        }

        int distance(int source, int target) {
            validateVertex(source, distances.length);
            validateVertex(target, distances.length);
            return distances[source][target];
        }

        List<Integer> path(int source, int target) {
            validateVertex(source, distances.length);
            validateVertex(target, distances.length);

            if (nextHop[source][target] == -1) {
                return Collections.emptyList();
            }

            List<Integer> path = new ArrayList<>();
            int current = source;
            path.add(current);

            while (current != target) {
                current = nextHop[current][target];
                if (current == -1) {
                    return Collections.emptyList();
                }
                path.add(current);
            }

            return path;
        }

        boolean hasNegativeCycle() {
            for (int i = 0; i < distances.length; i++) {
                if (distances[i][i] < 0) {
                    return true;
                }
            }
            return false;
        }
    }

    public static void main(String[] args) {
        Result shortestPaths = computeShortestPaths(sampleGraph());
        printMatrix(shortestPaths.distances());
    }
}
