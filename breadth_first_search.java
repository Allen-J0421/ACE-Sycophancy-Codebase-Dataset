import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

interface GraphView {
    int vertexCount();
    List<Integer> neighbors(int v);
}

class Graph implements GraphView {
    private final int vertices;
    private final List<List<Integer>> adj;

    Graph(int vertices) {
        this.vertices = vertices;
        adj = new ArrayList<>();
        for (int i = 0; i < vertices; i++)
            adj.add(new ArrayList<>());
    }

    void addEdge(int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    @Override
    public int vertexCount() {
        return vertices;
    }

    @Override
    public List<Integer> neighbors(int v) {
        return adj.get(v);
    }
}

class BFS {
    static List<Integer> traverse(GraphView graph) {
        int V = graph.vertexCount();
        boolean[] visited = new boolean[V];
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            if (!visited[i])
                traverseFrom(graph, i, visited, result);
        }
        return result;
    }

    private static void traverseFrom(GraphView graph, int src, boolean[] visited, List<Integer> result) {
        Queue<Integer> queue = new LinkedList<>();
        visited[src] = true;
        queue.add(src);
        while (!queue.isEmpty()) {
            int curr = queue.poll();
            result.add(curr);
            for (int neighbor : graph.neighbors(curr)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }
}

class BreadthFirstSearch {
    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(0, 3);
        graph.addEdge(4, 5);

        List<Integer> result = BFS.traverse(graph);
        for (int x : result)
            System.out.print(x + " ");
    }
}
