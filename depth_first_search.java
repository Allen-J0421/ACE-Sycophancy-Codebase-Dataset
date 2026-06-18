import java.util.ArrayList;

public class DepthFirstSearch {

    private static void
    dfsRec(ArrayList<ArrayList<Integer> > adj,
           boolean[] visited, int s, ArrayList<Integer> res) {
        visited[s] = true;
        res.add(s);

        for (int i : adj.get(s)) {
            if (!visited[i]) {
                dfsRec(adj, visited, i, res);
            }
        }
    }

    public static ArrayList<Integer>
    dfs(ArrayList<ArrayList<Integer> > adj) {
        boolean[] visited = new boolean[adj.size()];
        ArrayList<Integer> res = new ArrayList<>();

        for (int i = 0; i < adj.size(); i++) {
            if (!visited[i]) {
                dfsRec(adj, visited, i, res);
            }
        }

        return res;
    }

    static void addEdge(ArrayList<ArrayList<Integer>> adj, int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    public static void main(String[] args) {
        int V = 6;
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();

        for (int i = 0; i < V; i++)
            adj.add(new ArrayList<>());

        addEdge(adj, 1, 2);
        addEdge(adj, 0, 3);
        addEdge(adj, 2, 0);
        addEdge(adj, 5, 4);

        ArrayList<Integer> res = dfs(adj);

        for (int num : res) {
            System.out.print(num + " ");
        }
    }
}
