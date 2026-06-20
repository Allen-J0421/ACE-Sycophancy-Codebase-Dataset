import java.util.Arrays;

final class BellmanFord {
    private static final int INF = 100_000_000;
    private static final int NEGATIVE_CYCLE_MARKER = -1;
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
        int[] distances = initialDistances(graph.vertexCount, source);

        for (int pass = 1; pass < graph.vertexCount; pass++) {
            if (!graph.relaxEdges(distances)) {
                break;
            }
        }

        if (graph.hasReachableNegativeCycle(distances)) {
            return negativeCycleResult();
        }

        return distances;
    }

    private static int[] initialDistances(int vertexCount, int source) {
        int[] distances = new int[vertexCount];
        Arrays.fill(distances, INF);
        distances[source] = 0;
        return distances;
    }

    private static int[] negativeCycleResult() {
        return new int[]{NEGATIVE_CYCLE_MARKER};
    }

    private static String formatDistances(int[] distances) {
        StringBuilder output = new StringBuilder();
        for (int distance : distances) {
            output.append(distance).append(' ');
        }

        return output.toString();
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

        private boolean relax(int[] distances) {
            if (!canRelax(distances)) {
                return false;
            }

            distances[to] = distances[from] + weight;
            return true;
        }

        private boolean canRelax(int[] distances) {
            return distances[from] != INF && distances[from] + weight < distances[to];
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

        private boolean relaxEdges(int[] distances) {
            boolean changed = false;

            for (Edge edge : edges) {
                if (edge.relax(distances)) {
                    changed = true;
                }
            }

            return changed;
        }

        private boolean hasReachableNegativeCycle(int[] distances) {
            for (Edge edge : edges) {
                if (edge.canRelax(distances)) {
                    return true;
                }
            }

            return false;
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

        int[] distances = bellmanFord(vertexCount, edges, source);

        System.out.print(formatDistances(distances));
    }
}
