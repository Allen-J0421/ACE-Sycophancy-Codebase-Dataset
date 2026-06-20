import java.util.Arrays;

class BellmanFord {
    private static final int INF = 100_000_000;
    private static final int NEGATIVE_CYCLE = -1;

    static int[] bellmanFord(int V, int[][] edges, int src) {
        return bellmanFord(Graph.from(V, edges), src);
    }

    static int[] bellmanFord(Graph graph, int src) {
        return graph.shortestPathsFrom(src);
    }

    private static final class Graph {
        private final int vertices;
        private final Edge[] edges;

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

        static Graph of(int vertices, Edge... edges) {
            return new Graph(vertices, Arrays.copyOf(edges, edges.length));
        }

        int vertices() {
            return vertices;
        }

        int[] createDistances(int src) {
            int[] dist = new int[vertices];
            Arrays.fill(dist, INF);
            dist[src] = 0;
            return dist;
        }

        int[] shortestPathsFrom(int src) {
            int[] dist = createDistances(src);

            for (int pass = 0; pass < vertices - 1; pass++) {
                if (!relaxEdges(dist)) {
                    break;
                }
            }

            return hasNegativeCycle(dist) ? new int[]{NEGATIVE_CYCLE} : dist;
        }

        boolean relaxEdges(int[] dist) {
            boolean updated = false;
            for (Edge edge : edges) {
                if (edge.relax(dist)) {
                    updated = true;
                }
            }
            return updated;
        }

        boolean hasNegativeCycle(int[] dist) {
            for (Edge edge : edges) {
                if (edge.canRelax(dist)) {
                    return true;
                }
            }
            return false;
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

        boolean relax(int[] dist) {
            if (!canRelax(dist)) {
                return false;
            }
            dist[v] = dist[u] + wt;
            return true;
        }

        boolean canRelax(int[] dist) {
            return dist[u] != INF && dist[u] + wt < dist[v];
        }
    }

    public static void main(String[] args) {
        runSample();
    }

    private static void runSample() {
        int[] answer = bellmanFord(sampleGraph(), 0);
        printDistances(answer);
    }

    private static Graph sampleGraph() {
        return Graph.of(5,
            Edge.of(1, 3, 2),
            Edge.of(4, 3, -1),
            Edge.of(2, 4, 1),
            Edge.of(1, 2, 1),
            Edge.of(0, 1, 5));
    }

    private static void printDistances(int[] distances) {
        for (int distance : distances) {
            System.out.print(distance + " ");
        }
    }
}
