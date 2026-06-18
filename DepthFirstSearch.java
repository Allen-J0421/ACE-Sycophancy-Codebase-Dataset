import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DepthFirstSearch {

    private DepthFirstSearch() {
    }

    public static List<Integer> dfs(List<List<Integer>> adjacencyList) {
        boolean[] visitedVertices = new boolean[adjacencyList.size()];
        List<Integer> traversalOrder = new ArrayList<>();

        for (int vertex = 0; vertex < adjacencyList.size(); vertex++) {
            if (!visitedVertices[vertex]) {
                traverseComponent(adjacencyList, visitedVertices, vertex, traversalOrder);
            }
        }

        return traversalOrder;
    }

    private static void traverseComponent(
            List<List<Integer>> adjacencyList,
            boolean[] visitedVertices,
            int startVertex,
            List<Integer> traversalOrder) {
        Deque<Integer> pendingVertices = new ArrayDeque<>();
        pendingVertices.push(startVertex);

        while (!pendingVertices.isEmpty()) {
            int vertex = pendingVertices.pop();
            if (visitedVertices[vertex]) {
                continue;
            }

            visitedVertices[vertex] = true;
            traversalOrder.add(vertex);

            List<Integer> neighbors = adjacencyList.get(vertex);
            for (int index = neighbors.size() - 1; index >= 0; index--) {
                int neighbor = neighbors.get(index);
                if (!visitedVertices[neighbor]) {
                    pendingVertices.push(neighbor);
                }
            }
        }
    }

    public static void main(String[] args) {
        DepthFirstSearchDemo.main(args);
    }
}
