import java.util.ArrayList;

class Graph {
    private final int V;
    private final ArrayList<ArrayList<Integer>> adj;

    Graph(int V, int[][] edges) {
        this.V = V;
        adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }
    }

    int getV() { return V; }
    ArrayList<ArrayList<Integer>> getAdj() { return adj; }
}

interface GraphAlgorithm {
    ArrayList<Integer> execute();
}

class ArticulationPointsFinder implements GraphAlgorithm {
    private final Graph graph;

    ArticulationPointsFinder(Graph graph) {
        this.graph = graph;
    }

    private static class TraversalState {
        int[] disc, low, visited, isAP;
        int time;

        TraversalState(int V) {
            disc = new int[V];
            low = new int[V];
            visited = new int[V];
            isAP = new int[V];
            time = 0;
        }
    }

    private void dfs(int u, int parent, TraversalState state) {
        ArrayList<ArrayList<Integer>> adj = graph.getAdj();
        state.visited[u] = 1;
        state.disc[u] = state.low[u] = ++state.time;
        int children = 0;

        for (int v : adj.get(u)) {
            if (state.visited[v] == 0) {
                children++;
                dfs(v, u, state);

                state.low[u] = Math.min(state.low[u], state.low[v]);

                if (parent != -1 && state.low[v] >= state.disc[u]) {
                    state.isAP[u] = 1;
                }
            } else if (v != parent) {
                state.low[u] = Math.min(state.low[u], state.disc[v]);
            }
        }

        if (parent == -1 && children > 1) {
            state.isAP[u] = 1;
        }
    }

    public ArrayList<Integer> execute() {
        int V = graph.getV();
        TraversalState state = new TraversalState(V);

        for (int u = 0; u < V; u++) {
            if (state.visited[u] == 0) {
                dfs(u, -1, state);
            }
        }

        ArrayList<Integer> result = new ArrayList<>();
        for (int u = 0; u < V; u++) {
            if (state.isAP[u] == 1) result.add(u);
        }

        if (result.isEmpty()) result.add(-1);
        return result;
    }
}

class ArticulationPoints {
    public static void main(String[] args) {
        int V = 5;
        int[][] edges = {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}};

        Graph g = new Graph(V, edges);
        GraphAlgorithm finder = new ArticulationPointsFinder(g);
        ArrayList<Integer> ans = finder.execute();

        for (int u : ans) {
            System.out.print(u + " ");
        }
        System.out.println();
    }
}
