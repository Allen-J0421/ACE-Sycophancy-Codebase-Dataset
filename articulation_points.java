import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

class Graph {
    private final int V;
    private final ArrayList<ArrayList<Integer>> adj;

    private Graph(int V, ArrayList<ArrayList<Integer>> adj) {
        this.V = V;
        this.adj = adj;
    }

    static Graph of(int V, Iterable<int[]> edges) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }
        return new Graph(V, adj);
    }

    int getV() { return V; }
    ArrayList<ArrayList<Integer>> getAdj() { return adj; }
}

class ArticulationResult implements Iterable<Integer> {
    private final ArrayList<Integer> points;

    ArticulationResult(ArrayList<Integer> points) {
        this.points = points;
    }

    boolean hasArticulationPoints() { return !points.isEmpty(); }
    ArrayList<Integer> getPoints() { return points; }

    @Override
    public Iterator<Integer> iterator() { return points.iterator(); }
}

interface GraphAlgorithm<T> {
    T execute();
}

class ArticulationPointsFinder implements GraphAlgorithm<ArticulationResult> {
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

    public ArticulationResult execute() {
        int V = graph.getV();
        TraversalState state = new TraversalState(V);

        for (int u = 0; u < V; u++) {
            if (state.visited[u] == 0) {
                dfs(u, -1, state);
            }
        }

        ArrayList<Integer> points = new ArrayList<>();
        for (int u = 0; u < V; u++) {
            if (state.isAP[u] == 1) points.add(u);
        }

        return new ArticulationResult(points);
    }
}

class ArticulationPoints {
    public static void main(String[] args) {
        int V = 5;
        Iterable<int[]> edges = Arrays.asList(
            new int[]{0, 1}, new int[]{1, 4}, new int[]{2, 3},
            new int[]{2, 4}, new int[]{3, 4}
        );

        Graph g = Graph.of(V, edges);
        GraphAlgorithm<ArticulationResult> finder = new ArticulationPointsFinder(g);
        ArticulationResult result = finder.execute();

        for (int u : result) {
            System.out.print(u + " ");
        }
        System.out.println();
    }
}
