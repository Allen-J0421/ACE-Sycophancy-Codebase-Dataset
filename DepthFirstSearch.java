import java.util.ArrayList;
import java.util.List;

public class DepthFirstSearch {

    private DepthFirstSearch() {
    }

    public static List<Integer> dfs(List<List<Integer>> adjacencyList) {
        boolean[] visitedVertices = new boolean[adjacencyList.size()];
        List<Integer> traversalOrder = new ArrayList<>();

        for (int vertex = 0; vertex < adjacencyList.size(); vertex++) {
            if (!visitedVertices[vertex]) {
                dfsRecursive(adjacencyList, visitedVertices, vertex, traversalOrder);
            }
        }

        return traversalOrder;
    }

    private static void dfsRecursive(
            List<List<Integer>> adjacencyList,
            boolean[] visitedVertices,
            int vertex,
            List<Integer> traversalOrder) {
        visitedVertices[vertex] = true;
        traversalOrder.add(vertex);

        for (int neighbor : adjacencyList.get(vertex)) {
            if (!visitedVertices[neighbor]) {
                dfsRecursive(adjacencyList, visitedVertices, neighbor, traversalOrder);
            }
        }
    }

    public static void main(String[] args) {
        DepthFirstSearchDemo.main(args);
    }
}
