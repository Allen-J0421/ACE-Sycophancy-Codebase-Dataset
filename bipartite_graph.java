import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class BipartiteGraph {

    private static final int UNCOLORED = -1;
    private static final int FIRST_COLOR = 0;
    private static final int SECOND_COLOR = 1;

    private BipartiteGraph() {
    }

    static boolean isBipartite(int vertexCount, int[][] edges) {
        Graph graph = Graph.fromEdgeList(vertexCount, edges);
        int[] colors = new int[vertexCount];
        Arrays.fill(colors, UNCOLORED);

        for (int startVertex = 0; startVertex < vertexCount; startVertex++) {
            if (colors[startVertex] == UNCOLORED
                    && !colorComponent(startVertex, graph, colors)) {
                return false;
            }
        }

        return true;
    }

    private static boolean colorComponent(int startVertex, Graph graph, int[] colors) {
        Deque<Integer> queue = new ArrayDeque<>();
        colors[startVertex] = FIRST_COLOR;
        queue.addLast(startVertex);

        while (!queue.isEmpty()) {
            int currentVertex = queue.removeFirst();
            int nextColor = colors[currentVertex] == FIRST_COLOR ? SECOND_COLOR : FIRST_COLOR;

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                int neighborColor = colors[neighbor];
                if (neighborColor == UNCOLORED) {
                    colors[neighbor] = nextColor;
                    queue.addLast(neighbor);
                    continue;
                }

                if (neighborColor == colors[currentVertex]) {
                    return false;
                }
            }
        }

        return true;
    }

    private static final class Graph {
        private final List<List<Integer>> adjacencyList;

        private Graph(List<List<Integer>> adjacencyList) {
            this.adjacencyList = adjacencyList;
        }

        static Graph fromEdgeList(int vertexCount, int[][] edges) {
            validateGraphInput(vertexCount, edges);

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

            for (int i = 0; i < adjacencyList.size(); i++) {
                adjacencyList.set(i, Collections.unmodifiableList(adjacencyList.get(i)));
            }

            return new Graph(Collections.unmodifiableList(adjacencyList));
        }

        List<Integer> neighborsOf(int vertex) {
            return adjacencyList.get(vertex);
        }

        private static void validateGraphInput(int vertexCount, int[][] edges) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must be non-negative");
            }
            Objects.requireNonNull(edges, "edges");
        }

        private static void validateVertex(int vertex, int vertexCount) {
            if (vertex < 0 || vertex >= vertexCount) {
                throw new IllegalArgumentException(
                        "Vertex " + vertex + " is out of bounds for graph size " + vertexCount);
            }
        }
    }

    public static void main(String[] args) {
        int vertexCount = 4;
        int[][] edges = {{0, 1}, {0, 2}, {1, 2}, {2, 3}};

        System.out.println(isBipartite(vertexCount, edges));
    }
}
