import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AllPairsShortestPaths {

    private final IntSquareMatrix distances;
    private final IntSquareMatrix nextHop;

    AllPairsShortestPaths(int[][] distances, int[][] nextHop) {
        this(IntSquareMatrix.from(distances), IntSquareMatrix.from(nextHop));
    }

    AllPairsShortestPaths(IntSquareMatrix distances, IntSquareMatrix nextHop) {
        this.distances = distances;
        this.nextHop = nextHop;
    }

    public int vertexCount() {
        return distances.size();
    }

    public int[][] distances() {
        return distances.copyValues();
    }

    public int distance(int source, int target) {
        return distances.get(source, target);
    }

    public List<Integer> path(int source, int target) {
        if (nextHop.get(source, target) == -1) {
            return Collections.emptyList();
        }

        List<Integer> path = new ArrayList<>();
        int current = source;
        path.add(current);

        while (current != target) {
            current = nextHop.get(current, target);
            if (current == -1) {
                return Collections.emptyList();
            }
            path.add(current);
        }

        return Collections.unmodifiableList(path);
    }

    public boolean isReachable(int source, int target) {
        return nextHop.get(source, target) != -1;
    }

    public boolean hasNegativeCycle() {
        for (int i = 0; i < distances.size(); i++) {
            if (distances.get(i, i) < 0) {
                return true;
            }
        }
        return false;
    }
}
