import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

class KahnTopologicalSortStrategy implements TopologicalSortStrategy {

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

    @Override
    public List<Integer> sort(GraphView graph) {
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
