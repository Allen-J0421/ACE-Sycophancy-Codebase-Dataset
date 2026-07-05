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

class DfsService {
    private boolean[] visited;
    private final List<Integer> result = new ArrayList<>();

    public List<Integer> traverse(GraphInterface graph) {
        visited = new boolean[graph.size()];
        result.clear();
        for (int i = 0; i < graph.size(); i++) {
            if (!visited[i]) {
                visit(graph, i);
            }
        }
        return new ArrayList<>(result);
    }

    private void visit(GraphInterface graph, int vertex) {
        visited[vertex] = true;
        result.add(vertex);
        for (int neighbor : graph.getNeighbors(vertex)) {
            if (!visited[neighbor]) {
                visit(graph, neighbor);
            }
        }
    }
}

public class DepthFirstSearch {
    public static void main(String[] args) {
        Graph g = new Graph(6);
        g.addEdge(1, 2);
        g.addEdge(0, 3);
        g.addEdge(2, 0);
        g.addEdge(5, 4);

        DfsService service = new DfsService();
        List<Integer> result = service.traverse(g);

        for (int num : result) {
            System.out.print(num + " ");
        }
    }
}
