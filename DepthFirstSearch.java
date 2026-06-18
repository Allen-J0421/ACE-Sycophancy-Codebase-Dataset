import java.util.ArrayList;
import java.util.List;

public class DepthFirstSearch {

    private static final int VERTEX_COUNT = 6;
    private static final int[][] SAMPLE_EDGES = {
            {1, 2},
            {0, 3},
            {2, 0},
            {5, 4}
    };

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

    private static List<List<Integer>> buildSampleGraph() {
        List<List<Integer>> adjacencyList = createGraph(VERTEX_COUNT);

        for (int[] edge : SAMPLE_EDGES) {
            addUndirectedEdge(adjacencyList, edge[0], edge[1]);
        }

        return adjacencyList;
    }

    private static List<List<Integer>> createGraph(int vertexCount) {
        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }

        return adjacencyList;
    }

    private static void addUndirectedEdge(List<List<Integer>> adjacencyList, int source, int destination) {
        adjacencyList.get(source).add(destination);
        adjacencyList.get(destination).add(source);
    }

    private static void printTraversal(List<Integer> traversalOrder) {
        for (int vertex : traversalOrder) {
            System.out.print(vertex + " ");
        }
    }

    public static void main(String[] args) {
        List<List<Integer>> adjacencyList = buildSampleGraph();
        List<Integer> traversalOrder = dfs(adjacencyList);

        printTraversal(traversalOrder);
    }
}
