import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

class CycleDetectedException extends RuntimeException {
    CycleDetectedException() {
        super("Graph contains a cycle and cannot be topologically sorted");
    }
}

class TopologicalSortService {
    private final GraphView graph;

    private TopologicalSortService(GraphView graph) {
        this.graph = graph;
    }

    static TopologicalSortService of(GraphView graph) {
        return new TopologicalSortService(graph);
    }

    private int[] computeIndegrees() {
        int n = graph.size();
        int[] indegree = new int[n];
        for (int i = 0; i < n; i++) {
            for (int next : graph.getNeighbors(i)) {
                indegree[next]++;
            }
        }
        return indegree;
    }

    List<Integer> sort() {
        int n = graph.size();
        int[] indegree = computeIndegrees();
        Queue<Integer> q = new LinkedList<>();
        List<Integer> result = new ArrayList<>();

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

        if (result.size() != n) {
            throw new CycleDetectedException();
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

        List<Integer> res = TopologicalSortService.of(graph).sort();
        for (int vertex : res) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
