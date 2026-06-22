import java.util.ArrayList;
import java.util.List;

class ArticulationPoints {

    static class Graph {
        private final int V;
        private final List<List<Integer>> adj;

        Graph(int V, int[][] edges) {
            if (V <= 0) throw new IllegalArgumentException("V must be positive");
            this.V = V;
            adj = new ArrayList<>();
            for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
            for (int[] edge : edges) {
                adj.get(edge[0]).add(edge[1]);
                adj.get(edge[1]).add(edge[0]);
            }
        }

        int size() { return V; }

        List<Integer> neighbors(int u) { return adj.get(u); }
    }

    private static class DfsState {
        final int[] disc;
        final int[] low;
        final boolean[] visited;
        final boolean[] isAP;
        int time;

        DfsState(int V) {
            disc = new int[V];
            low = new int[V];
            visited = new boolean[V];
            isAP = new boolean[V];
        }
    }

    // Tarjan's DFS to find articulation points.
    // A non-root vertex u is an AP if some child v has no back-edge reaching above u (low[v] >= disc[u]).
    // A root is an AP if it has more than one DFS-tree child.
    private static void dfs(Graph g, DfsState state, int u, int parent) {
        state.visited[u] = true;
        state.disc[u] = state.low[u] = ++state.time;
        int children = 0;

        for (int v : g.neighbors(u)) {
            if (!state.visited[v]) {
                children++;
                dfs(g, state, v, u);
                state.low[u] = Math.min(state.low[u], state.low[v]);
                if (parent != -1 && state.low[v] >= state.disc[u]) {
                    state.isAP[u] = true;
                }
            } else if (v != parent) {
                state.low[u] = Math.min(state.low[u], state.disc[v]);
            }
        }

        if (parent == -1 && children > 1) {
            state.isAP[u] = true;
        }
    }

    static List<Integer> articulationPoints(Graph g) {
        DfsState state = new DfsState(g.size());

        for (int u = 0; u < g.size(); u++) {
            if (!state.visited[u]) {
                dfs(g, state, u, -1);
            }
        }

        List<Integer> result = new ArrayList<>();
        for (int u = 0; u < g.size(); u++) {
            if (state.isAP[u]) result.add(u);
        }
        return result;
    }

    public static void main(String[] args) {
        Graph g = new Graph(5, new int[][]{{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}});
        List<Integer> ans = articulationPoints(g);

        if (ans.isEmpty()) {
            System.out.println(-1);
        } else {
            for (int u : ans) System.out.print(u + " ");
            System.out.println();
        }
    }
}
