import java.util.ArrayList;
import java.util.List;

interface GraphInterface {
    List<Integer> getNeighbors(int vertex);
    int size();
}

class Graph implements GraphInterface {
    private final ArrayList<ArrayList<Integer>> adj;

    Graph(int vertices) {
        adj = new ArrayList<>();
        for (int i = 0; i < vertices; i++)
            adj.add(new ArrayList<>());
    }

    public void addEdge(int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    @Override
    public List<Integer> getNeighbors(int vertex) {
        return adj.get(vertex);
    }

    @Override
    public int size() {
        return adj.size();
    }
}

public class DepthFirstSearch {

    private static void dfsRec(GraphInterface graph, boolean[] visited, int s, ArrayList<Integer> res) {
        visited[s] = true;
        res.add(s);

        for (int i : graph.getNeighbors(s)) {
            if (!visited[i]) {
                dfsRec(graph, visited, i, res);
            }
        }
    }

    public static ArrayList<Integer> dfs(GraphInterface graph) {
        boolean[] visited = new boolean[graph.size()];
        ArrayList<Integer> res = new ArrayList<>();

        for (int i = 0; i < graph.size(); i++) {
            if (!visited[i]) {
                dfsRec(graph, visited, i, res);
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

        ArrayList<Integer> res = dfs(g);

        for (int num : res) {
            System.out.print(num + " ");
        }
    }
}
