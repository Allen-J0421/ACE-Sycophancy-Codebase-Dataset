import java.util.Arrays;

class BellmanFord {
    private static final int INF = 100_000_000;
    private static final int NEGATIVE_CYCLE = -1;

    static int[] bellmanFord(int V, int[][] edges, int src) {
        return bellmanFord(Graph.from(V, edges), src);
    }

    static int[] bellmanFord(Graph graph, int src) {
        int[] dist = createDistances(graph.vertices, src);

        for (int pass = 0; pass < graph.vertices - 1; pass++) {
            if (!relaxEdges(dist, graph.edges)) {
                break;
            }
        }

        return hasNegativeCycle(dist, graph.edges) ? new int[]{NEGATIVE_CYCLE} : dist;
    }

    private static int[] createDistances(int vertices, int src) {
        int[] dist = new int[vertices];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        return dist;
    }

    private static boolean relaxEdges(int[] dist, Edge[] edges) {
        boolean updated = false;
        for (Edge edge : edges) {
            if (relaxEdge(dist, edge)) {
                updated = true;
            }
        }
        return updated;
    }

    private static boolean hasNegativeCycle(int[] dist, Edge[] edges) {
        for (Edge edge : edges) {
            if (canRelax(dist, edge)) {
                return true;
            }
        }
        return false;
    }

    private static boolean relaxEdge(int[] dist, Edge edge) {
        if (!canRelax(dist, edge)) {
            return false;
        }
        dist[edge.v] = dist[edge.u] + edge.wt;
        return true;
    }

    private static boolean canRelax(int[] dist, Edge edge) {
        return dist[edge.u] != INF && dist[edge.u] + edge.wt < dist[edge.v];
    }

    private static final class Graph {
        final int vertices;
        final Edge[] edges;

        private Graph(int vertices, Edge[] edges) {
            this.vertices = vertices;
            this.edges = edges;
        }

        static Graph from(int vertices, int[][] edges) {
            Edge[] typedEdges = new Edge[edges.length];
            for (int i = 0; i < edges.length; i++) {
                typedEdges[i] = Edge.of(edges[i][0], edges[i][1], edges[i][2]);
            }
            return new Graph(vertices, typedEdges);
        }

        static Graph from(int vertices, Edge[] edges) {
            return new Graph(vertices, edges);
        }
    }

    static final class Edge {
        final int u;
        final int v;
        final int wt;

        private Edge(int u, int v, int wt) {
            this.u = u;
            this.v = v;
            this.wt = wt;
        }

        static Edge of(int u, int v, int wt) {
            return new Edge(u, v, wt);
        }
    }

    public static void main(String[] args) {
        runSample();
    }

    private static void runSample() {
        int[] answer = bellmanFord(Graph.from(5, sampleEdges()), 0);
        printDistances(answer);
    }

    private static Edge[] sampleEdges() {
        return new Edge[] {
            Edge.of(1, 3, 2),
            Edge.of(4, 3, -1),
            Edge.of(2, 4, 1),
            Edge.of(1, 2, 1),
            Edge.of(0, 1, 5)
        };
    }

    private static void printDistances(int[] distances) {
        for (int distance : distances) {
            System.out.print(distance + " ");
        }
    }
}
