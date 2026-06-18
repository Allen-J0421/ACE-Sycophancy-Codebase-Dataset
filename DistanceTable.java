import java.util.ArrayList;
import java.util.Arrays;

final class DistanceTable {
    private static final int INFINITE_DISTANCE = Integer.MAX_VALUE;

    private final int[] distances;

    private DistanceTable(int vertexCount, int source) {
        distances = new int[vertexCount];
        Arrays.fill(distances, INFINITE_DISTANCE);
        distances[source] = 0;
    }

    static DistanceTable withSource(int vertexCount, int source) {
        return new DistanceTable(vertexCount, source);
    }

    boolean hasShorterPathTo(int vertex, int distance) {
        return distance > distances[vertex];
    }

    boolean tryRelax(int from, Edge edge) {
        if (distances[from] > INFINITE_DISTANCE - edge.weight()) {
            return false;
        }

        int candidateDistance = distances[from] + edge.weight();
        if (candidateDistance >= distances[edge.destination()]) {
            return false;
        }

        distances[edge.destination()] = candidateDistance;
        return true;
    }

    int distanceTo(int vertex) {
        return distances[vertex];
    }

    ArrayList<Integer> toList() {
        ArrayList<Integer> result = new ArrayList<>();
        for (int distance : distances) {
            result.add(distance);
        }
        return result;
    }
}
