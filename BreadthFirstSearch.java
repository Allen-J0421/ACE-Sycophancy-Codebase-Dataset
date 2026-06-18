import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class BreadthFirstSearch implements GraphTraversal {
    private final boolean[] visited;
    private final List<Integer> result;
    private int componentCount;

    public BreadthFirstSearch() {
        this.visited = null;
        this.result = null;
        this.componentCount = 0;
    }

    @Override
    public TraversalResult traverse(Graph graph) {
        boolean[] visited = new boolean[graph.getVertexCount()];
        List<Integer> result = new ArrayList<>();
        int components = 0;

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                bfsFromVertex(graph, i, visited, result);
                components++;
            }
        }

        return new TraversalResult(result, components);
    }

    private void bfsFromVertex(Graph graph, int startVertex, boolean[] visited, List<Integer> result) {
        Queue<Integer> queue = new LinkedList<>();
        visited[startVertex] = true;
        queue.add(startVertex);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            result.add(current);

            for (int neighbor : graph.getAdjacent(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }
}
