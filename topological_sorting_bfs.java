import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

interface GraphView {
    int size();
    List<Integer> getNeighbors(int u);
}

class Graph implements GraphView {
    private final int n;
    private final ArrayList<ArrayList<Integer>> adj;

    Graph(int n) {
        this.n = n;
        adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    void addEdge(int u, int v) {
        adj.get(u).add(v);
    }

    @Override
    public List<Integer> getNeighbors(int u) {
        return adj.get(u);
    }

    @Override
    public int size() {
        return n;
    }
}

class TopologicalSortService {
    private final GraphView graph;

    TopologicalSortService(GraphView graph) {
        this.graph = graph;
    }

    ArrayList<Integer> sort() {
        int n = graph.size();
        int[] indegree = new int[n];
        Queue<Integer> q = new LinkedList<>();
        ArrayList<Integer> result = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            for (int next : graph.getNeighbors(i)) {
                indegree[next]++;
            }
        }

        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                q.add(i);
            }
        }

        while (!q.isEmpty()) {
            int top = q.poll();
            result.add(top);
            for (int next : graph.getNeighbors(top)) {
                indegree[next]--;
                if (indegree[next] == 0) {
                    q.add(next);
                }
            }
        }

        return result;
    }
}

class TopologicalSort {

    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(4, 5);
        graph.addEdge(5, 1);
        graph.addEdge(5, 2);

        ArrayList<Integer> res = new TopologicalSortService(graph).sort();
        for (int vertex : res) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
