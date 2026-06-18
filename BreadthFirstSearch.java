import java.util.LinkedList;
import java.util.Queue;

public class BreadthFirstSearch extends AbstractGraphTraversal {

    @Override
    public String getAlgorithmName() {
        return "BFS";
    }

    @Override
    protected void traverseFromVertex(int startVertex) {
        Queue<Integer> queue = new LinkedList<>();
        markVisited(startVertex);
        queue.offer(startVertex);

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            addToResult(vertex);

            for (int neighbor : graph.getNeighbors(vertex)) {
                if (!isVisited(neighbor)) {
                    markVisited(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
    }
}
