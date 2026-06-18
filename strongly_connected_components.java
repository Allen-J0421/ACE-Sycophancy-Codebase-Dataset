import java.util.Stack;

class StronglyConnectedComponents {

    void DFS1(int u, int[][] adj, boolean[] visited, Stack<Integer> st) {
        visited[u] = true;
        for (int v : adj[u]) {
            if (v == -1) break;
            if (!visited[v]) DFS1(v, adj, visited, st);
        }
        st.push(u);
    }

    void DFS2(int u, int[][] revAdj, boolean[] visited, int[] scc, int[] idx) {
        visited[u] = true;
        scc[idx[0]++] = u;
        for (int v : revAdj[u]) {
            if (v == -1) break;
            if (!visited[v]) DFS2(v, revAdj, visited, scc, idx);
        }
    }

    int[][] kosaraju(int V, int[][] adj) {
        boolean[] visited = new boolean[V];
        Stack<Integer> st = new Stack<>();

        for (int i = 0; i < V; i++) {
            if (!visited[i]) DFS1(i, adj, visited, st);
        }

        int[][] revAdj = new int[V][V];
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) revAdj[i][j] = -1;

        }

        int[] count = new int[V];

        for (int u = 0; u < V; u++) {
            for (int v : adj[u]) {
                if (v == -1) break;
                revAdj[v][count[v]++] = u;
            }
        }

        for (int i = 0; i < V; i++) visited[i] = false;

        int[][] SCCs = new int[V][V];
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) SCCs[i][j] = -1;
        }

        int sccCount = 0;
        while (!st.isEmpty()) {
            int u = st.pop();
            if (!visited[u]) {
                int[] scc = new int[V];
                for (int i = 0; i < V; i++) scc[i] = -1;
                int[] idx = {0};

                DFS2(u, revAdj, visited, scc, idx);
                SCCs[sccCount++] = scc;
            }
        }

        int[][] result = new int[sccCount][];
        for (int i = 0; i < sccCount; i++) result[i] = SCCs[i];
        return result;
    }

    static int[][] buildAdjMatrix(int[][] edges, int V) {
        int[][] adj = new int[V + 1][V + 1];
        for (int i = 0; i <= V; i++) {
            for (int j = 0; j <= V; j++) {
                adj[i][j] = -1;
            }
        }

        int[] count = new int[V + 1];
        for (int[] edge : edges) {
            int u = edge[0], v = edge[1];
            adj[u][count[u]++] = v;
        }

        return adj;
    }

    public static void main(String[] args) {
        StronglyConnectedComponents obj = new StronglyConnectedComponents();
        int V = 5;
        int[][] edges = {
            {1, 3}, {1, 4}, {2, 1}, {3, 2}, {4, 5}
        };

        int[][] adj = buildAdjMatrix(edges, V);

        int[][] SCCs = obj.kosaraju(V + 1, adj);

        System.out.println("Strongly Connected Components:");
        for (int i = 0; i < SCCs.length - 1; i++) {
            for (int j = 0; j < SCCs[i].length && SCCs[i][j] != -1; j++) {
                System.out.print(SCCs[i][j] + " ");
            }
            System.out.println();
        }
    }
}
