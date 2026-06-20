import java.util.ArrayList;
import java.util.List;

public final class Edge {
    private final int source;
    private final int destination;

    private Edge(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }

    public static Edge between(int source, int destination) {
        return new Edge(source, destination);
    }

    static List<Edge> fromPairs(int[][] edgePairs) {
        requireEdgePairs(edgePairs);

        List<Edge> edges = new ArrayList<>(edgePairs.length);
        for (int edgeIndex = 0; edgeIndex < edgePairs.length; edgeIndex++) {
            edges.add(fromPair(edgePairs[edgeIndex], edgeIndex));
        }

        return edges;
    }

    private static Edge fromPair(int[] edgePair, int edgeIndex) {
        if (edgePair == null || edgePair.length != 2) {
            throw new IllegalArgumentException(
                    "Each edge must contain exactly two vertices. Invalid edge at index "
                            + edgeIndex
                            + ".");
        }

        return new Edge(edgePair[0], edgePair[1]);
    }

    private static void requireEdgePairs(int[][] edgePairs) {
        if (edgePairs == null) {
            throw new IllegalArgumentException("Edges cannot be null.");
        }
    }

    public int source() {
        return source;
    }

    public int destination() {
        return destination;
    }
}
