import java.util.Arrays;

class BellmanFord {
    private static final int INF = 100_000_000;
    private static final int NEGATIVE_CYCLE = -1;

    static int[] bellmanFord(int V, int[][] edges, int src) {
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        for (int i = 0; i < V - 1; i++) {
            relaxEdges(dist, edges);
        }

        return hasNegativeCycle(dist, edges) ? new int[]{NEGATIVE_CYCLE} : dist;
    }

    private static void relaxEdges(int[] dist, int[][] edges) {
        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            int wt = edge[2];

            if (dist[u] != INF && dist[u] + wt < dist[v]) {
                dist[v] = dist[u] + wt;
            }
        }
    }

    private static boolean hasNegativeCycle(int[] dist, int[][] edges) {
        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            int wt = edge[2];

            if (dist[u] != INF && dist[u] + wt < dist[v]) {
                return true;
            }
        }
        return false;
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
