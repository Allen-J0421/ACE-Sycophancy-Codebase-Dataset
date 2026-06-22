import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
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
                if (edge[0] < 0 || edge[0] >= V || edge[1] < 0 || edge[1] >= V)
                    throw new IllegalArgumentException("Edge endpoint out of range");
                adj.get(edge[0]).add(edge[1]);
                adj.get(edge[1]).add(edge[0]);
            }
        }

        int size() { return V; }

        List<Integer> neighbors(int u) { return Collections.unmodifiableList(adj.get(u)); }

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
            final List<Integer> nbrs;
            int iterIndex;
            int children;

            Frame(int u, int parent, List<Integer> nbrs) {
                this.u = u;
                this.parent = parent;
                this.nbrs = nbrs;
            }
        }

        private void dfsFrom(DfsState state, int root) {
            Deque<Frame> stack = new ArrayDeque<>();
            state.visited[root] = true;
            state.disc[root] = state.low[root] = ++state.time;
            stack.push(new Frame(root, -1, adj.get(root)));

            while (!stack.isEmpty()) {
                Frame frame = stack.peek();

                if (frame.iterIndex < frame.nbrs.size()) {
                    int v = frame.nbrs.get(frame.iterIndex++);
                    if (!state.visited[v]) {
                        frame.children++;
                        state.visited[v] = true;
                        state.disc[v] = state.low[v] = ++state.time;
                        stack.push(new Frame(v, frame.u, adj.get(v)));
                    } else if (v != frame.parent) {
                        state.low[frame.u] = Math.min(state.low[frame.u], state.disc[v]);
                    }
                } else {
                    stack.pop();
                    int u = frame.u;
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

        // Tarjan's iterative DFS to find articulation points.
        // A non-root vertex u is an AP if some child v has no back-edge reaching above u (low[v] >= disc[u]).
        // A root is an AP if it has more than one DFS-tree child.
        List<Integer> findArticulationPoints() {
            DfsState state = new DfsState(V);

            for (int u = 0; u < V; u++) {
                if (!state.visited[u]) dfsFrom(state, u);
            }

            List<Integer> result = new ArrayList<>();
            for (int u = 0; u < V; u++) {
                if (state.isAP[u]) result.add(u);
            }
            return result;
        }
    }

    public static void main(String[] args) {
        Graph g = new Graph(5, new int[][]{{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}});
        List<Integer> ans = g.findArticulationPoints();

        if (ans.isEmpty()) {
            System.out.println(-1);
        } else {
            for (int u : ans) System.out.print(u + " ");
            System.out.println();
        }
    }
}
