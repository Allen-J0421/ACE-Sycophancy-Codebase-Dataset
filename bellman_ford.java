import java.util.Arrays;

final class BellmanFord {
    private static final int INF = 100_000_000;
    private static final int FROM_INDEX = 0;
    private static final int TO_INDEX = 1;
    private static final int WEIGHT_INDEX = 2;
    private static final int EDGE_FIELD_COUNT = 3;

    private BellmanFord() {
    }

    static int[] bellmanFord(int vertexCount, int[][] edgeData, int source) {
        Graph graph = Graph.from(vertexCount, edgeData);
        graph.validateSource(source);
        return shortestPathsFrom(graph, source);
    }

    private static int[] shortestPathsFrom(Graph graph, int source) {
        int[] dist = new int[graph.vertexCount];
        Arrays.fill(dist, INF);
        dist[source] = 0;

        for (int i = 1; i < graph.vertexCount; i++) {
            if (!relaxEdges(graph.edges, dist)) {
                break;
            }
        }

        if (hasReachableNegativeCycle(graph.edges, dist)) {
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

    private static final class Graph {
        private final int vertexCount;
        private final Edge[] edges;

        private Graph(int vertexCount, Edge[] edges) {
            this.vertexCount = vertexCount;
            this.edges = edges;
        }

        private static Graph from(int vertexCount, int[][] edgeData) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("Vertex count cannot be negative");
            }

            if (edgeData == null) {
                throw new IllegalArgumentException("Edges cannot be null");
            }

            Edge[] edges = new Edge[edgeData.length];
            for (int i = 0; i < edgeData.length; i++) {
                edges[i] = parseEdge(vertexCount, edgeData[i], i);
            }

            return new Graph(vertexCount, edges);
        }

        private static Edge parseEdge(int vertexCount, int[] edgeData, int index) {
            if (edgeData == null || edgeData.length != EDGE_FIELD_COUNT) {
                throw new IllegalArgumentException("Edge " + index + " must contain from, to, and weight");
            }

            Edge edge = new Edge(edgeData[FROM_INDEX], edgeData[TO_INDEX], edgeData[WEIGHT_INDEX]);
            if (!edge.isWithin(vertexCount)) {
                throw new IllegalArgumentException("Edge " + index + " references a vertex outside the graph");
            }

            return edge;
        }

        private void validateSource(int source) {
            if (source < 0 || source >= vertexCount) {
                throw new IllegalArgumentException("Source vertex is out of range");
            }
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

        int source = 0;

        int[] ans = bellmanFord(vertexCount, edges, source);

        for (int dist : ans) {
            System.out.print(dist + " ");
        }
    }
}
