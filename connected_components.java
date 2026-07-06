import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

interface Graph {
    int size();
    Iterable<Integer> getNeighbors(int v);
}

class AdjacencyListGraph implements Graph {
    private ArrayList<ArrayList<Integer>> adj;
    private int V;

    AdjacencyListGraph(int V) {
        this.V = V;
        adj = new ArrayList<>();
        for (int i = 0; i < V; i++)
            adj.add(new ArrayList<>());
    }

    void addEdge(int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    @Override
    public int size() {
        return V;
    }

    @Override
    public Iterable<Integer> getNeighbors(int v) {
        return adj.get(v);
    }
}

class GraphTraversal {

    static void bfs(Graph g, int src, boolean[] visited, ArrayList<Integer> res) {
        Queue<Integer> q = new LinkedList<>();
        visited[src] = true;
        q.add(src);

        while (!q.isEmpty()) {
            int curr = q.poll();
            res.add(curr);

            for (int x : g.getNeighbors(curr)) {
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
                GraphTraversal.bfs(g, i, visited, component);
                res.add(component);
            }
        }
        return res;
    }

    public static void main(String[] args) {
        AdjacencyListGraph g = new AdjacencyListGraph(6);

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
