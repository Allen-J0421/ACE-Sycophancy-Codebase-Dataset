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

    private TopologicalSortService() {}

    static TopologicalSortService of() {
        return new TopologicalSortService();
    }

    private int[] computeIndegrees(GraphView graph) {
        int n = graph.size();
        int[] indegree = new int[n];
        for (int i = 0; i < n; i++) {
            for (int next : graph.getNeighbors(i)) {
                indegree[next]++;
            }
        }
        return indegree;
    }

    private List<Integer> processQueue(GraphView graph, int[] indegree, Queue<Integer> q) {
        List<Integer> result = new ArrayList<>();
        while (!q.isEmpty()) {
            int top = q.poll();
            result.add(top);
            for (int next : graph.getNeighbors(top)) {
                if (--indegree[next] == 0) {
                    q.add(next);
                }
            }
        }
        return result;
    }

    List<Integer> sort(GraphView graph) {
        int n = graph.size();
        int[] indegree = computeIndegrees(graph);
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                q.add(i);
            }
        }

        List<Integer> result = processQueue(graph, indegree, q);

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

        List<Integer> res = TopologicalSortService.of().sort(graph);
        for (int vertex : res) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
