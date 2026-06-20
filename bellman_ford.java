import java.util.Arrays;

class BellmanFord {
    private static final int INF = 100_000_000;
    private static final int NEGATIVE_CYCLE = -1;

    static int[] bellmanFord(int V, int[][] edges, int src) {
        Edge[] typedEdges = toEdges(edges);
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        for (int pass = 0; pass < V - 1; pass++) {
            if (!relaxEdges(dist, typedEdges)) {
                break;
            }
        }

        return hasNegativeCycle(dist, typedEdges) ? new int[]{NEGATIVE_CYCLE} : dist;
    }

    private static Edge[] toEdges(int[][] edges) {
        Edge[] typedEdges = new Edge[edges.length];
        for (int i = 0; i < edges.length; i++) {
            typedEdges[i] = new Edge(edges[i][0], edges[i][1], edges[i][2]);
        }
        return typedEdges;
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

    private static final class Edge {
        private final int u;
        private final int v;
        private final int wt;

        private Edge(int u, int v, int wt) {
            this.u = u;
            this.v = v;
            this.wt = wt;
        }
    }

    public static void main(String[] args) {

        int V = 5;

        int[][] edges = new int[][] {
            {1, 3, 2},
            {4, 3, -1},
            {2, 4, 1},
            {1, 2, 1},
            {0, 1, 5}
        };

        int src = 0;

        int[] ans = bellmanFord(V, edges, src);

        for (int dist : ans)
            System.out.print(dist + " ");
    }
}
