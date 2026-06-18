import java.util.Arrays;

class DistanceTable {
    private static final int INFINITY = Integer.MAX_VALUE;
    private final int[] distances;
    private final int sourceNode;

    private DistanceTable(int vertexCount, int sourceNode) {
        this.distances = new int[vertexCount];
        Arrays.fill(distances, INFINITY);
        this.sourceNode = sourceNode;
        this.distances[sourceNode] = 0;
    }

    static DistanceTable create(int vertexCount, int sourceNode) {
        return new DistanceTable(vertexCount, sourceNode);
    }

    void updateDistance(int node, int newDistance) {
        distances[node] = newDistance;
    }

    int getDistance(int node) {
        return distances[node];
    }

    int[] toArray() {
        return distances.clone();
    }

    @Override
    public String toString() {
        return Arrays.toString(distances);
    }
}
