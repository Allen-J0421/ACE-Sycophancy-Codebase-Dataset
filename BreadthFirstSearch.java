import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BreadthFirstSearch implements GraphTraversal {

    @Override
    public List<Integer> traverse(Graph graph) {
        boolean[] visited = new boolean[graph.getVertexCount()];
        List<Integer> result = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                bfsFromVertex(graph, i, visited, result, queue);
            }
        }

        return result;
    }

    private void bfsFromVertex(Graph graph, int start, boolean[] visited, List<Integer> result, Queue<Integer> queue) {
        queue.offer(start);
        visited[start] = true;

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            result.add(vertex);

            for (int neighbor : graph.getNeighbors(vertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                }
            }
        }
    }
}
