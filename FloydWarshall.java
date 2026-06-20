import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FloydWarshall {

    public static final int DEFAULT_UNREACHABLE_DISTANCE = 100_000_000;

    private final int[][] graph;
    private final int unreachableDistance;

    private FloydWarshall(int[][] graph, int unreachableDistance) {
        validateSquareMatrix(graph);
        validateUnreachableDistance(unreachableDistance);

        this.graph = copyMatrix(graph);
        this.unreachableDistance = unreachableDistance;
    }

    public static FloydWarshall from(int[][] graph) {
        return new FloydWarshall(graph, DEFAULT_UNREACHABLE_DISTANCE);
    }

    public static FloydWarshall from(int[][] graph, int unreachableDistance) {
        return new FloydWarshall(graph, unreachableDistance);
    }

    public int vertexCount() {
        return graph.length;
    }

    public Result solve() {
        int[][] distances = copyMatrix(graph);
        int[][] nextHop = new int[graph.length][graph.length];
        initializeNextHop(nextHop);

        int vertices = distances.length;
        for (int k = 0; k < vertices; k++) {
            for (int i = 0; i < vertices; i++) {
                if (distances[i][k] == unreachableDistance) {
                    continue;
                }

                for (int j = 0; j < vertices; j++) {
                    if (distances[k][j] == unreachableDistance) {
                        continue;
                    }

                    long throughK = (long) distances[i][k] + distances[k][j];
                    if (throughK < distances[i][j]) {
                        distances[i][j] = (int) throughK;
                        nextHop[i][j] = nextHop[i][k];
                    }
                }
            }
        }

        return new Result(distances, nextHop);
    }

    private void initializeNextHop(int[][] nextHop) {
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph.length; j++) {
                nextHop[i][j] = graph[i][j] == unreachableDistance ? -1 : j;
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

    private static void validateUnreachableDistance(int unreachableDistance) {
        if (unreachableDistance <= 0) {
            throw new IllegalArgumentException("Unreachable distance must be positive.");
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

    public static final class Result {
        private final int[][] distances;
        private final int[][] nextHop;

        private Result(int[][] distances, int[][] nextHop) {
            this.distances = copyMatrix(distances);
            this.nextHop = copyMatrix(nextHop);
        }

        public int vertexCount() {
            return distances.length;
        }

        public int[][] distances() {
            return copyMatrix(distances);
        }

        public int distance(int source, int target) {
            validateVertex(source, distances.length);
            validateVertex(target, distances.length);
            return distances[source][target];
        }

        public List<Integer> path(int source, int target) {
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

            return Collections.unmodifiableList(path);
        }

        public boolean isReachable(int source, int target) {
            validateVertex(source, distances.length);
            validateVertex(target, distances.length);
            return nextHop[source][target] != -1;
        }

        public boolean hasNegativeCycle() {
            for (int i = 0; i < distances.length; i++) {
                if (distances[i][i] < 0) {
                    return true;
                }
            }
            return false;
        }
    }
}
