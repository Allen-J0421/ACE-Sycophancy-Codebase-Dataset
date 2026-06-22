import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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

    private static class Frame {
        final int u;
        final int parent;
        int iterIndex;
        int children;

        Frame(int u, int parent) {
            this.u = u;
            this.parent = parent;
        }
    }

    // Tarjan's iterative DFS to find articulation points.
    // A non-root vertex u is an AP if some child v has no back-edge reaching above u (low[v] >= disc[u]).
    // A root is an AP if it has more than one DFS-tree child.
    private static void dfs(Graph g, DfsState state, int root) {
        Deque<Frame> stack = new ArrayDeque<>();
        state.visited[root] = true;
        state.disc[root] = state.low[root] = ++state.time;
        stack.push(new Frame(root, -1));

        while (!stack.isEmpty()) {
            Frame frame = stack.peek();
            int u = frame.u;
            List<Integer> nbrs = g.neighbors(u);

            if (frame.iterIndex < nbrs.size()) {
                int v = nbrs.get(frame.iterIndex++);
                if (!state.visited[v]) {
                    frame.children++;
                    state.visited[v] = true;
                    state.disc[v] = state.low[v] = ++state.time;
                    stack.push(new Frame(v, u));
                } else if (v != frame.parent) {
                    state.low[u] = Math.min(state.low[u], state.disc[v]);
                }
            } else {
                stack.pop();
                if (!stack.isEmpty()) {
                    int p = stack.peek().u;
                    state.low[p] = Math.min(state.low[p], state.low[u]);
                    if (stack.peek().parent != -1 && state.low[u] >= state.disc[p]) {
                        state.isAP[p] = true;
                    }
                }
                if (frame.parent == -1 && frame.children > 1) {
                    state.isAP[u] = true;
                }
            }
        }
    }

    static List<Integer> articulationPoints(Graph g) {
        DfsState state = new DfsState(g.size());

        for (int u = 0; u < g.size(); u++) {
            if (!state.visited[u]) {
                dfs(g, state, u);
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
