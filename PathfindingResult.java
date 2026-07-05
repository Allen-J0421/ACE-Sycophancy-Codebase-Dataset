import java.util.List;

record PathfindingResult(List<Integer> distances) {
    static final int UNREACHABLE = Integer.MAX_VALUE;

    int distanceTo(int vertex) {
        return distances.get(vertex);
    }

    boolean isReachable(int vertex) {
        return distanceTo(vertex) != UNREACHABLE;
    }
}
