import java.util.Arrays;

final class BellmanFord {
    private static final int INF = 100_000_000;
    private static final int FROM_INDEX = 0;
    private static final int TO_INDEX = 1;
    private static final int WEIGHT_INDEX = 2;
    private static final int EDGE_FIELD_COUNT = 3;

    private BellmanFord() {
    }

    static int[] bellmanFord(int vertexCount, int[][] edgeData, int src) {
        validateVertexCountAndSource(vertexCount, src);
        Edge[] edges = parseEdges(vertexCount, edgeData);

        int[] dist = new int[vertexCount];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        for (int i = 1; i < vertexCount; i++) {
            if (!relaxEdges(edges, dist)) {
                break;
            }
        }

        if (hasReachableNegativeCycle(edges, dist)) {
            return new int[]{-1};
        }

        return dist;
    }

    private static boolean relaxEdges(Edge[] edges, int[] dist) {
        boolean changed = false;

        for (Edge edge : edges) {
            if (canRelax(dist, edge)) {
                dist[edge.to] = dist[edge.from] + edge.weight;
                changed = true;
            }
        }

        return changed;
    }

    private static boolean hasReachableNegativeCycle(Edge[] edges, int[] dist) {
        for (Edge edge : edges) {
            if (canRelax(dist, edge)) {
                return true;
            }
        }

        return false;
    }

    private static boolean canRelax(int[] dist, Edge edge) {
        return dist[edge.from] != INF && dist[edge.from] + edge.weight < dist[edge.to];
    }

    private static void validateVertexCountAndSource(int vertexCount, int src) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative");
        }

        if (src < 0 || src >= vertexCount) {
            throw new IllegalArgumentException("Source vertex is out of range");
        }
    }

    private static Edge[] parseEdges(int vertexCount, int[][] edges) {
        if (edges == null) {
            throw new IllegalArgumentException("Edges cannot be null");
        }

        Edge[] parsedEdges = new Edge[edges.length];
        for (int i = 0; i < edges.length; i++) {
            int[] edge = edges[i];
            if (edge == null || edge.length != EDGE_FIELD_COUNT) {
                throw new IllegalArgumentException("Edge " + i + " must contain from, to, and weight");
            }

            parsedEdges[i] = new Edge(edge[FROM_INDEX], edge[TO_INDEX], edge[WEIGHT_INDEX]);
            if (!parsedEdges[i].isWithin(vertexCount)) {
                throw new IllegalArgumentException("Edge " + i + " references a vertex outside the graph");
            }
        }

        return parsedEdges;
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

        private boolean isWithin(int vertexCount) {
            return from >= 0 && from < vertexCount && to >= 0 && to < vertexCount;
        }
    }

    public static void main(String[] args) {

        int vertexCount = 5;

        int[][] edges = new int[][] {
            {1, 3, 2},
            {4, 3, -1},
            {2, 4, 1},
            {1, 2, 1},
            {0, 1, 5}
        };

        int src = 0;

        int[] ans = bellmanFord(vertexCount, edges, src);

        for (int dist : ans) {
            System.out.print(dist + " ");
        }
    }
}
