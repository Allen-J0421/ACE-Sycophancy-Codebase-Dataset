import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

class Graph {
    private ArrayList<ArrayList<Integer>> adj;
    private int V;

    Graph(int V) {
        this.V = V;
        adj = new ArrayList<>();
        for (int i = 0; i < V; i++)
            adj.add(new ArrayList<>());
    }

    void addEdge(int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    int size() {
        return V;
    }

    void bfs(int src, boolean[] visited, ArrayList<Integer> res) {
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
}

class ConnectedComponents {

    static ArrayList<ArrayList<Integer>> getComponents(Graph g) {
        int V = g.size();
        boolean[] visited = new boolean[V];
        ArrayList<ArrayList<Integer>> res = new ArrayList<>();

        for (int i = 0; i < V; i++) {
            if (!visited[i]) {
                ArrayList<Integer> component = new ArrayList<>();
                g.bfs(i, visited, component);
                res.add(component);
            }
        }
        return res;
    }

    public static void main(String[] args) {
        Graph g = new Graph(6);

        g.addEdge(1, 2);
        g.addEdge(0, 3);
        g.addEdge(2, 0);
        g.addEdge(5, 4);

        ArrayList<ArrayList<Integer>> res = getComponents(g);

        for (ArrayList<Integer> component : res) {
            for (int vertex : component) {
                System.out.print(vertex + " ");
            }
            System.out.println();
        }
    }
}
