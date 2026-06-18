import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class BreadthFirstSearch {
    private final UndirectedGraph graph;
    private final boolean[] visited;
    private final List<Integer> result;

    public BreadthFirstSearch(UndirectedGraph graph) {
        this.graph = graph;
        this.visited = new boolean[graph.getVertexCount()];
        this.result = new ArrayList<>();
    }

    public List<Integer> traverse() {
        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                bfsFromVertex(i);
            }
        }
        return result;
    }

    private void bfsFromVertex(int startVertex) {
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
