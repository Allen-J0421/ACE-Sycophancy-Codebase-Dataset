import java.util.ArrayList;
import java.util.List;

public class DepthFirstSearch {

    private static final int VERTEX_COUNT = 6;

    private DepthFirstSearch() {
    }

    private static void dfsRecursive(
            List<List<Integer>> adjacencyList,
            boolean[] visited,
            int vertex,
            List<Integer> traversalOrder) {
        visited[vertex] = true;
        traversalOrder.add(vertex);

        for (int neighbor : adjacencyList.get(vertex)) {
            if (!visited[neighbor]) {
                dfsRecursive(adjacencyList, visited, neighbor, traversalOrder);
            }
        }
    }

    public static List<Integer> dfs(List<List<Integer>> adjacencyList) {
        boolean[] visited = new boolean[adjacencyList.size()];
        List<Integer> traversalOrder = new ArrayList<>();

        for (int vertex = 0; vertex < adjacencyList.size(); vertex++) {
            if (!visited[vertex]) {
                dfsRecursive(adjacencyList, visited, vertex, traversalOrder);
            }
        }

        return traversalOrder;
    }

    private static List<List<Integer>> createGraph(int vertexCount) {
        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }

        return adjacencyList;
    }

    private static void addEdge(List<List<Integer>> adjacencyList, int source, int destination) {
        adjacencyList.get(source).add(destination);
        adjacencyList.get(destination).add(source);
    }

    public static void main(String[] args) {
        List<List<Integer>> adjacencyList = createGraph(VERTEX_COUNT);

        addEdge(adjacencyList, 1, 2);
        addEdge(adjacencyList, 0, 3);
        addEdge(adjacencyList, 2, 0);
        addEdge(adjacencyList, 5, 4);

        List<Integer> traversalOrder = dfs(adjacencyList);
        for (int vertex : traversalOrder) {
            System.out.print(vertex + " ");
        }
    }
}
