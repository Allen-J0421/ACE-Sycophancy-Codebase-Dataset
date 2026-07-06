import java.util.ArrayList;

class ArticulationPoints {

    static class TraversalState {
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

    static ArrayList<ArrayList<Integer>> constructAdj(int V, int[][] edges) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }
        return adj;
    }

    static void findPoints(ArrayList<ArrayList<Integer>> adj, int u, int parent, TraversalState state) {

        state.visited[u] = 1;
        state.disc[u] = state.low[u] = ++state.time;
        int children = 0;

        for (int v : adj.get(u)) {
            if (state.visited[v] == 0) {
                children++;
                findPoints(adj, v, u, state);

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

    static ArrayList<Integer> articulationPoints(int V, ArrayList<ArrayList<Integer>> adj) {

        TraversalState state = new TraversalState(V);

        for (int u = 0; u < V; u++) {
            if (state.visited[u] == 0) {
                findPoints(adj, u, -1, state);
            }
        }

        ArrayList<Integer> result = new ArrayList<>();
        for (int u = 0; u < V; u++) {
            if (state.isAP[u] == 1) {
                result.add(u);
            }
        }

        if (result.isEmpty()) result.add(-1);
        return result;
    }

    public static void main(String[] args) {
        int V = 5;

        int[][] edges = {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}};

        ArrayList<ArrayList<Integer>> adj = constructAdj(V, edges);
        ArrayList<Integer> ans = articulationPoints(V, adj);

        for (int u : ans) {
            System.out.print(u + " ");
        }
        System.out.println();
    }
}
