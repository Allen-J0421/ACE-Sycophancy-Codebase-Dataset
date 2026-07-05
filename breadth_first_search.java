import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class Graph {
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

    List<Integer> bfs() {
        boolean[] visited = new boolean[vertices];
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            if (!visited[i])
                bfsFrom(i, visited, result);
        }
        return result;
    }

    private void bfsFrom(int src, boolean[] visited, List<Integer> result) {
        Queue<Integer> queue = new LinkedList<>();
        visited[src] = true;
        queue.add(src);
        while (!queue.isEmpty()) {
            int curr = queue.poll();
            result.add(curr);
            for (int neighbor : adj.get(curr)) {
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

        List<Integer> result = graph.bfs();
        for (int x : result)
            System.out.print(x + " ");
    }
}
