import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

class BreadthFirstSearch {

    static void bfsConnected(ArrayList<ArrayList<Integer>> adj, int src, boolean[] visited, ArrayList<Integer> res) {
        Queue<Integer> q = new LinkedList<>();
        visited[src] = true;
        q.add(src);

        while (!q.isEmpty()) {
            int curr = q.poll();
            res.add(curr);

            for (int x : adj.get(curr)) {
                if (!visited[x]) {
                    visited[x] = true;
                    q.add(x);
                }
            }
        }
    }

    static ArrayList<Integer> bfs(ArrayList<ArrayList<Integer>> adj) {
        int V = adj.size();
        boolean[] visited = new boolean[V];
        ArrayList<Integer> res = new ArrayList<>();

        for (int i = 0; i < V; i++) {
            if (!visited[i])
                bfsConnected(adj, i, visited, res);
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
        addEdge(adj, 2, 0);
        addEdge(adj, 0, 3);
        addEdge(adj, 4, 5);

        ArrayList<Integer> res = bfs(adj);

        for (int x : res)
            System.out.print(x + " ");
    }
}
