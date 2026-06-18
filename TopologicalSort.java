import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

class Graph {
    private final int n;
    private final List<List<Integer>> adj;

    Graph(int n) {
        this.n = n;
        adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }

    void addEdge(int u, int v) {
        adj.get(u).add(v);
    }

    List<Integer> topoSort() {
        int[] indegree = new int[n];

        for (int u = 0; u < n; u++) {
            for (int v : adj.get(u)) {
                indegree[v]++;
            }
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) queue.add(i);
        }

        List<Integer> result = new ArrayList<>(n);
        while (!queue.isEmpty()) {
            int node = queue.poll();
            result.add(node);
            for (int neighbor : adj.get(node)) {
                if (--indegree[neighbor] == 0) queue.add(neighbor);
            }
        }

        if (result.size() != n) {
            throw new IllegalStateException("Graph contains a cycle");
        }

        return result;
    }
}

class TopologicalSort {
    public static void main(String[] args) {
        Graph g = new Graph(6);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(4, 5);
        g.addEdge(5, 1);
        g.addEdge(5, 2);

        System.out.println(g.topoSort());
    }
}
