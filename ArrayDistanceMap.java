import java.util.Arrays;

final class ArrayDistanceMap implements DistanceMap {

    private final int[] dist;

    ArrayDistanceMap(int vertices) {
        dist = new int[vertices];
        Arrays.fill(dist, Distances.UNREACHABLE);
    }

    @Override
    public void set(int vertex, int distance) {
        dist[vertex] = distance;
    }

    @Override
    public int get(int vertex) {
        return dist[vertex];
    }

    @Override
    public int size() {
        return dist.length;
    }

    @Override
    public int[] snapshot() {
        return dist.clone();
    }
}
