import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class BreadthFirstSearch extends AbstractTraversal {

    @Override
    protected void traverseComponent(Graph graph, int startVertex, boolean[] visited, List<Integer> result, int componentId) {
        Queue<Integer> queue = new LinkedList<>();
        visited[startVertex] = true;
        queue.add(startVertex);
        publishEvent(TraversalEvent.vertexVisited(startVertex, componentId));

        while (!queue.isEmpty()) {
            int current = queue.poll();
            result.add(current);

            for (int neighbor : graph.getAdjacent(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                    publishEvent(TraversalEvent.vertexVisited(neighbor, componentId));
                }
            }
        }
    }
}
