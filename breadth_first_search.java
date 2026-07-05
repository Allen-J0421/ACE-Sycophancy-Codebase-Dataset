import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

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
    static void traverse(GraphView graph, Consumer<Integer> visitor) {
        int V = graph.vertexCount();
        boolean[] visited = new boolean[V];
        for (int i = 0; i < V; i++) {
            if (!visited[i])
                traverseFrom(graph, i, visited, visitor);
        }
    }

    private static void traverseFrom(GraphView graph, int src, boolean[] visited, Consumer<Integer> visitor) {
        Queue<Integer> queue = new LinkedList<>();
        visited[src] = true;
        queue.add(src);
        while (!queue.isEmpty()) {
            int curr = queue.poll();
            visitor.accept(curr);
            for (int neighbor : graph.neighbors(curr)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }

    static List<Integer> collect(GraphView graph) {
        List<Integer> result = new ArrayList<>();
        traverse(graph, result::add);
        return result;
    }
}

class BreadthFirstSearch {
    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(0, 3);
        graph.addEdge(4, 5);

        BFS.traverse(graph, node -> System.out.print(node + " "));
    }
}
