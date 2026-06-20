import java.util.Arrays;

final class BellmanFord {
    private static final int INF = 100_000_000;

    private BellmanFord() {
    }

    static int[] bellmanFord(int vertices, int[][] edgeData, int source) {
        validateInput(vertices, edgeData, source);
        return bellmanFord(vertices, toEdges(edgeData), source);
    }

    private static int[] bellmanFord(int vertices, Edge[] edges, int source) {
        int[] distances = new int[vertices];
        Arrays.fill(distances, INF);
        distances[source] = 0;

        for (int pass = 0; pass < vertices - 1; pass++) {
            boolean updated = false;

            for (Edge edge : edges) {
                if (distances[edge.from] == INF) {
                    continue;
                }

                int candidateDistance = distances[edge.from] + edge.weight;
                if (candidateDistance < distances[edge.to]) {
                    distances[edge.to] = candidateDistance;
                    updated = true;
                }
            }

            if (!updated) {
                return distances;
            }
        }

        for (Edge edge : edges) {
            if (distances[edge.from] != INF
                    && distances[edge.from] + edge.weight < distances[edge.to]) {
                return new int[] {-1};
            }
        }

        return distances;
    }

    private static void validateInput(int vertices, int[][] edgeData, int source) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("vertices must be positive");
        }
        if (source < 0 || source >= vertices) {
            throw new IllegalArgumentException("source must be within the vertex range");
        }
        if (edgeData == null) {
            throw new IllegalArgumentException("edges must not be null");
        }
    }

    private static Edge[] toEdges(int[][] edgeData) {
        Edge[] edges = new Edge[edgeData.length];
        for (int i = 0; i < edgeData.length; i++) {
            int[] edge = edgeData[i];
            if (edge == null || edge.length != 3) {
                throw new IllegalArgumentException("each edge must contain exactly 3 integers");
            }
            edges[i] = new Edge(edge[0], edge[1], edge[2]);
        }
        return edges;
    }

    private static final class Edge {
        private final int from;
        private final int to;
        private final int weight;

        private Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    public static void main(String[] args) {
        int vertices = 5;
        int[][] edges = {
            {1, 3, 2},
            {4, 3, -1},
            {2, 4, 1},
            {1, 2, 1},
            {0, 1, 5}
        };
        int source = 0;

        int[] shortestDistances = bellmanFord(vertices, edges, source);
        for (int distance : shortestDistances) {
            System.out.print(distance + " ");
        }
    }
}
