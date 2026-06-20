import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

final class BipartiteGraph {

    private static final int UNCOLORED = -1;
    private static final int FIRST_COLOR = 0;

    private BipartiteGraph() {
    }

    static boolean isBipartite(int vertexCount, int[][] edges) {
        validateInput(vertexCount, edges);

        List<List<Integer>> adjacencyList = buildAdjacencyList(vertexCount, edges);
        int[] colors = new int[vertexCount];
        Arrays.fill(colors, UNCOLORED);

        for (int startVertex = 0; startVertex < vertexCount; startVertex++) {
            if (colors[startVertex] == UNCOLORED
                    && !colorComponent(startVertex, adjacencyList, colors)) {
                return false;
            }
        }

        return true;
    }

    private static void validateInput(int vertexCount, int[][] edges) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }
        Objects.requireNonNull(edges, "edges");
    }

    private static List<List<Integer>> buildAdjacencyList(int vertexCount, int[][] edges) {
        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            if (edge == null || edge.length != 2) {
                throw new IllegalArgumentException("Each edge must contain exactly two vertices");
            }

            int source = edge[0];
            int target = edge[1];
            validateVertex(source, vertexCount);
            validateVertex(target, vertexCount);

            adjacencyList.get(source).add(target);
            adjacencyList.get(target).add(source);
        }

        return adjacencyList;
    }

    private static void validateVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                    "Vertex " + vertex + " is out of bounds for graph size " + vertexCount);
        }
    }

    private static boolean colorComponent(int startVertex, List<List<Integer>> adjacencyList, int[] colors) {
        Deque<Integer> queue = new ArrayDeque<>();
        colors[startVertex] = FIRST_COLOR;
        queue.addLast(startVertex);

        while (!queue.isEmpty()) {
            int currentVertex = queue.removeFirst();
            int nextColor = 1 - colors[currentVertex];

            for (int neighbor : adjacencyList.get(currentVertex)) {
                if (colors[neighbor] == UNCOLORED) {
                    colors[neighbor] = nextColor;
                    queue.addLast(neighbor);
                } else if (colors[neighbor] == colors[currentVertex]) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void main(String[] args) {
        int vertexCount = 4;
        int[][] edges = {{0, 1}, {0, 2}, {1, 2}, {2, 3}};

        System.out.println(isBipartite(vertexCount, edges));
    }
}
