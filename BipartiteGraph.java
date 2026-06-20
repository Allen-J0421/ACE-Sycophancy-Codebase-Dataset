import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Collections;
import java.util.List;

public final class BipartiteGraph {
    private static final int UNCOLORED = -1;
    private static final int FIRST_COLOR = 0;
    private static final int SECOND_COLOR = 1;

    private BipartiteGraph() {
        // Utility class.
    }

    static boolean isBipartite(int vertexCount, int[][] edges) {
        return isBipartite(Graph.fromEdges(vertexCount, edges));
    }

    static boolean isBipartite(Graph graph) {
        int[] colors = new int[graph.vertexCount()];
        Arrays.fill(colors, UNCOLORED);

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (colors[vertex] == UNCOLORED
                    && !isComponentBipartite(vertex, graph, colors)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isComponentBipartite(
            int startVertex,
            Graph graph,
            int[] colors) {
        Deque<Integer> queue = new ArrayDeque<>();
        colors[startVertex] = FIRST_COLOR;
        queue.offer(startVertex);

        while (!queue.isEmpty()) {
            int currentVertex = queue.poll();

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (colors[neighbor] == UNCOLORED) {
                    colors[neighbor] = oppositeColor(colors[currentVertex]);
                    queue.offer(neighbor);
                    continue;
                }

                if (colors[neighbor] == colors[currentVertex]) {
                    return false;
                }
            }
        }

        return true;
    }

    private static int oppositeColor(int color) {
        return color == FIRST_COLOR ? SECOND_COLOR : FIRST_COLOR;
    }

    static final class Graph {
        private final List<List<Integer>> adjacencyList;

        private Graph(List<List<Integer>> adjacencyList) {
            this.adjacencyList = adjacencyList;
        }

        static Graph fromEdges(int vertexCount, int[][] edges) {
            validateInputs(vertexCount, edges);

            List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacencyList.add(new ArrayList<>());
            }

            for (int[] edge : edges) {
                int source = edge[0];
                int destination = edge[1];
                adjacencyList.get(source).add(destination);
                adjacencyList.get(destination).add(source);
            }

            return new Graph(toUnmodifiableAdjacencyList(adjacencyList));
        }

        int vertexCount() {
            return adjacencyList.size();
        }

        List<Integer> neighborsOf(int vertex) {
            return adjacencyList.get(vertex);
        }

        private static List<List<Integer>> toUnmodifiableAdjacencyList(
                List<List<Integer>> adjacencyList) {
            List<List<Integer>> immutableAdjacencyList = new ArrayList<>(adjacencyList.size());
            for (List<Integer> neighbors : adjacencyList) {
                immutableAdjacencyList.add(Collections.unmodifiableList(neighbors));
            }

            return Collections.unmodifiableList(immutableAdjacencyList);
        }
    }

    private static void validateInputs(int vertexCount, int[][] edges) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }

        if (edges == null) {
            throw new IllegalArgumentException("Edges cannot be null.");
        }

        for (int edgeIndex = 0; edgeIndex < edges.length; edgeIndex++) {
            validateEdge(edges[edgeIndex], vertexCount, edgeIndex);
        }
    }

    private static void validateEdge(int[] edge, int vertexCount, int edgeIndex) {
        if (edge == null || edge.length != 2) {
            throw new IllegalArgumentException(
                    "Each edge must contain exactly two vertices. Invalid edge at index "
                            + edgeIndex
                            + ".");
        }

        validateVertex(edge[0], vertexCount, edgeIndex);
        validateVertex(edge[1], vertexCount, edgeIndex);
    }

    private static void validateVertex(int vertex, int vertexCount, int edgeIndex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                    "Edge at index "
                            + edgeIndex
                            + " references invalid vertex "
                            + vertex
                            + " for graph size "
                            + vertexCount
                            + ".");
        }
    }

    public static void main(String[] args) {
        int vertexCount = 4;
        int[][] edges = {{0, 1}, {0, 2}, {1, 2}, {2, 3}};

        System.out.println(isBipartite(vertexCount, edges));
    }
}
