import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class FloydWarshall {

    static final int INF = Integer.MAX_VALUE / 2;

    static class Result {
        private final int[][] dist;
        private final int[][] next;

        private Result(int[][] dist, int[][] next) {
            this.dist = dist;
            this.next = next;
        }

        int getDistance(int from, int to) {
            return dist[from][to];
        }

        boolean isReachable(int from, int to) {
            return dist[from][to] < INF;
        }

        List<Integer> getPath(int from, int to) {
            if (!isReachable(from, to)) {
                return Collections.emptyList();
            }
            List<Integer> path = new ArrayList<>();
            for (int v = from; v != to; v = next[v][to]) {
                path.add(v);
            }
            path.add(to);
            return path;
        }

        int size() {
            return dist.length;
        }
    }

    static Result compute(int[][] graph) {
        int n = graph.length;
        if (n == 0) {
            throw new IllegalArgumentException("Graph must be non-empty");
        }
        for (int[] row : graph) {
            if (row.length != n) {
                throw new IllegalArgumentException("Graph must be a square matrix");
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

        return new Result(dist, next);
    }
}
