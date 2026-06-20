import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FloydWarshall {

    public static final int INF = Integer.MAX_VALUE / 2;

    public static class Result {
        private final int[][] dist;
        private final int[][] next;

        private Result(int[][] dist, int[][] next) {
            this.dist = dist;
            this.next = next;
        }

        private void checkVertex(int v) {
            if (v < 0 || v >= dist.length) {
                throw new IndexOutOfBoundsException(
                    "Vertex " + v + " out of range [0, " + (dist.length - 1) + "]");
            }
        }

        public int getDistance(int from, int to) {
            checkVertex(from);
            checkVertex(to);
            return dist[from][to];
        }

        public boolean isReachable(int from, int to) {
            checkVertex(from);
            checkVertex(to);
            return dist[from][to] < INF;
        }

        public List<Integer> getPath(int from, int to) {
            checkVertex(from);
            checkVertex(to);
            if (dist[from][to] >= INF) {
                return Collections.emptyList();
            }
            int n = dist.length;
            List<Integer> path = new ArrayList<>();
            for (int v = from; v != to; ) {
                if (path.size() >= n) {
                    throw new IllegalStateException("Path reconstruction loop; graph may contain a negative cycle");
                }
                int nxt = next[v][to];
                if (nxt < 0) {
                    throw new IllegalStateException("Broken next-hop entry at vertex " + v);
                }
                path.add(v);
                v = nxt;
            }
            path.add(to);
            return path;
        }

        public int vertexCount() {
            return dist.length;
        }
    }

    public static Result compute(int[][] graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null");
        }
        int n = graph.length;
        if (n == 0) {
            throw new IllegalArgumentException("Graph must be non-empty");
        }
        for (int[] row : graph) {
            if (row == null || row.length != n) {
                throw new IllegalArgumentException("Graph must be a square matrix with no null rows");
            }
        }

        int[][] dist = new int[n][n];
        int[][] next = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = graph[i][j];
                next[i][j] = (graph[i][j] < INF && i != j) ? j : -1;
            }
            dist[i][i] = 0;
            next[i][i] = i;
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] < INF && dist[k][j] < INF
                            && dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }

        for (int i = 0; i < n; i++) {
            if (dist[i][i] < 0) {
                throw new IllegalArgumentException("Graph contains a negative-weight cycle");
            }
        }

        return new Result(dist, next);
    }
}
