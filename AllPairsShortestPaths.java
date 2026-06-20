import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AllPairsShortestPaths {

    private final int[][] distances;
    private final int[][] nextHop;

    AllPairsShortestPaths(int[][] distances, int[][] nextHop) {
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
}
