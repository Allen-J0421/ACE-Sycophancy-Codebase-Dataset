import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

final class BipartiteGraph {
    private BipartiteGraph() {
    }

    /**
     * @deprecated Use {@link #constructAdjacencyList(int, int[][])}.
     */
    @Deprecated
    static ArrayList<ArrayList<Integer>> constructadj(int vertexCount, int[][] edges) {
        return constructAdjacencyList(vertexCount, edges);
    }

    static ArrayList<ArrayList<Integer>> constructAdjacencyList(int vertexCount, int[][] edges) {
        return Graph.fromEdges(vertexCount, edges).toAdjacencyList();
    }

    static boolean isBipartite(int vertexCount, int[][] edges) {
        Graph graph = Graph.fromEdges(vertexCount, edges);
        return isBipartite(graph);
    }

    private static boolean isBipartite(Graph graph) {
        Color[] colors = createColorAssignments(graph.vertexCount());

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (colors[vertex].isUncolored() && !colorComponent(vertex, graph, colors)) {
                return false;
            }
        }

        return true;
    }

    private static Color[] createColorAssignments(int vertexCount) {
        Color[] colors = new Color[vertexCount];
        Arrays.fill(colors, Color.UNCOLORED);
        return colors;
    }

    private static boolean colorComponent(
            int startVertex,
            Graph graph,
            Color[] colors
    ) {
        Queue<Integer> queue = new ArrayDeque<>();
        colors[startVertex] = Color.FIRST;
        queue.offer(startVertex);

        while (!queue.isEmpty()) {
            int currentVertex = queue.poll();
            Color currentColor = colors[currentVertex];

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (colors[neighbor].isUncolored()) {
                    colors[neighbor] = currentColor.opposite();
                    queue.offer(neighbor);
                } else if (colors[neighbor] == currentColor) {
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

    private static final class Graph {
        private static final int EDGE_ENDPOINT_COUNT = 2;
        private static final int SOURCE_VERTEX_INDEX = 0;
        private static final int TARGET_VERTEX_INDEX = 1;

        private final List<List<Integer>> adjacencyList;

        private Graph(List<List<Integer>> adjacencyList) {
            this.adjacencyList = adjacencyList;
        }

        private static Graph fromEdges(int vertexCount, int[][] edges) {
            validateInput(vertexCount, edges);

            List<List<Integer>> adjacencyList = createEmptyAdjacencyList(vertexCount);

            for (int[] edge : edges) {
                addUndirectedEdge(
                        adjacencyList,
                        edge[SOURCE_VERTEX_INDEX],
                        edge[TARGET_VERTEX_INDEX]
                );
            }

            return new Graph(adjacencyList);
        }

        private int vertexCount() {
            return adjacencyList.size();
        }

        private List<Integer> neighborsOf(int vertex) {
            return adjacencyList.get(vertex);
        }

        private ArrayList<ArrayList<Integer>> toAdjacencyList() {
            ArrayList<ArrayList<Integer>> copy = new ArrayList<>(adjacencyList.size());

            for (List<Integer> neighbors : adjacencyList) {
                copy.add(new ArrayList<>(neighbors));
            }

            return copy;
        }

        private static List<List<Integer>> createEmptyAdjacencyList(int vertexCount) {
            List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);

            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacencyList.add(new ArrayList<>());
            }

            return adjacencyList;
        }

        private static void addUndirectedEdge(
                List<List<Integer>> adjacencyList,
                int sourceVertex,
                int targetVertex
        ) {
            adjacencyList.get(sourceVertex).add(targetVertex);
            adjacencyList.get(targetVertex).add(sourceVertex);
        }

        private static void validateInput(int vertexCount, int[][] edges) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must be non-negative");
            }

            if (edges == null) {
                throw new IllegalArgumentException("edges must not be null");
            }

            for (int edgeIndex = 0; edgeIndex < edges.length; edgeIndex++) {
                validateEdge(edges[edgeIndex], edgeIndex, vertexCount);
            }
        }

        private static void validateEdge(int[] edge, int edgeIndex, int vertexCount) {
            if (edge == null || edge.length != EDGE_ENDPOINT_COUNT) {
                throw new IllegalArgumentException(
                        "edge at index " + edgeIndex + " must contain exactly two vertices"
                );
            }

            validateVertex(edge[SOURCE_VERTEX_INDEX], edgeIndex, vertexCount);
            validateVertex(edge[TARGET_VERTEX_INDEX], edgeIndex, vertexCount);
        }

        private static void validateVertex(int vertex, int edgeIndex, int vertexCount) {
            if (vertex < 0 || vertex >= vertexCount) {
                throw new IllegalArgumentException(
                        "edge at index " + edgeIndex + " references invalid vertex " + vertex
                );
            }
        }
    }

    private enum Color {
        UNCOLORED,
        FIRST,
        SECOND;

        private boolean isUncolored() {
            return this == UNCOLORED;
        }

        private Color opposite() {
            if (this == FIRST) {
                return SECOND;
            }

            if (this == SECOND) {
                return FIRST;
            }

            throw new IllegalStateException("Uncolored vertices do not have an opposite color");
        }
    }
}
