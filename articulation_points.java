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
            private final int[] disc;
            private final int[] low;
            private final boolean[] visited;
            private final boolean[] isAP;
            private int time;

            DfsState(int V) {
                disc = new int[V];
                low = new int[V];
                visited = new boolean[V];
                isAP = new boolean[V];
            }

            void discover(int u) { visited[u] = true; disc[u] = low[u] = ++time; }

            boolean isVisited(int u) { return visited[u]; }

            void updateLowFromDescendant(int u, int child) { low[u] = Math.min(low[u], low[child]); }

            void updateLowFromAncestor(int u, int ancestor) { low[u] = Math.min(low[u], disc[ancestor]); }

            boolean isBottleneck(int child, int parent) { return low[child] >= disc[parent]; }

            void markAP(int u) { isAP[u] = true; }

            boolean[] results() { return isAP; }
        }

        private static class Frame {
            static final int NO_PARENT = -1;
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

        // Tarjan's iterative DFS to find articulation points.
        // A non-root vertex u is an AP if some child v has no back-edge reaching above u (low[v] >= disc[u]).
        // A root is an AP if it has more than one DFS-tree child.
        private void dfsFrom(DfsState state, int root) {
            Deque<Frame> stack = new ArrayDeque<>();
            state.discover(root);
            stack.push(new Frame(root, Frame.NO_PARENT, adj.get(root)));

            while (!stack.isEmpty()) {
                Frame frame = stack.peek();

                if (frame.iterIndex < frame.nbrs.size()) {
                    int v = frame.nbrs.get(frame.iterIndex++);
                    if (!state.isVisited(v)) {
                        frame.children++;
                        state.discover(v);
                        stack.push(new Frame(v, frame.u, adj.get(v)));
                    } else if (v != frame.parent) {
                        state.updateLowFromAncestor(frame.u, v);
                    }
                } else {
                    stack.pop();
                    int u = frame.u;
                    if (!stack.isEmpty()) {
                        int p = stack.peek().u;
                        state.updateLowFromDescendant(p, u);
                        if (stack.peek().parent != -1 && state.isBottleneck(u, p)) {
                            state.markAP(p);
                        }
                    }
                    if (frame.parent == Frame.NO_PARENT && frame.children > 1) {
                        state.markAP(u);
                    }
                }
            }
        }

        List<Integer> findArticulationPoints() {
            DfsState state = new DfsState(V);
            for (int u = 0; u < V; u++) {
                if (!state.isVisited(u)) dfsFrom(state, u);
            }
            boolean[] isAP = state.results();
            List<Integer> result = new ArrayList<>();
            for (int u = 0; u < V; u++) {
                if (isAP[u]) result.add(u);
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
            System.out.println(String.join(" ", ans.stream().map(String::valueOf).toArray(String[]::new)));
        }
    }
}
