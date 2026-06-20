import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

class BipartiteGraph {
    static ArrayList<ArrayList<Integer>> constructadj(int vertexCount, int[][] edges) {
        validateInput(vertexCount, edges);

        ArrayList<ArrayList<Integer>> adjacencyList = createEmptyAdjacencyList(vertexCount);

        for (int[] edge : edges) {
            addUndirectedEdge(adjacencyList, edge[0], edge[1]);
        }

        return adjacencyList;
    }

    static boolean isBipartite(int vertexCount, int[][] edges) {
        ArrayList<ArrayList<Integer>> adjacencyList = constructadj(vertexCount, edges);

        Color[] colors = new Color[vertexCount];
        Arrays.fill(colors, Color.UNCOLORED);

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (colors[vertex] == Color.UNCOLORED && !colorComponent(vertex, adjacencyList, colors)) {
                return false;
            }
        }

        return true;
    }

    private static boolean colorComponent(
            int startVertex,
            List<? extends List<Integer>> adjacencyList,
            Color[] colors
    ) {
        Queue<Integer> queue = new ArrayDeque<>();
        colors[startVertex] = Color.FIRST;
        queue.offer(startVertex);

        while (!queue.isEmpty()) {
            int currentVertex = queue.poll();

            for (int neighbor : adjacencyList.get(currentVertex)) {
                if (colors[neighbor] == Color.UNCOLORED) {
                    colors[neighbor] = colors[currentVertex].opposite();
                    queue.offer(neighbor);
                } else if (colors[neighbor] == colors[currentVertex]) {
                    return false;
                }
            }
        }

        return true;
    }

    private static ArrayList<ArrayList<Integer>> createEmptyAdjacencyList(int vertexCount) {
        ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<>();

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }

        return adjacencyList;
    }

    private static void addUndirectedEdge(
            ArrayList<ArrayList<Integer>> adjacencyList,
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
        if (edge == null || edge.length != 2) {
            throw new IllegalArgumentException("edge at index " + edgeIndex + " must contain exactly two vertices");
        }

        validateVertex(edge[0], edgeIndex, vertexCount);
        validateVertex(edge[1], edgeIndex, vertexCount);
    }

    private static void validateVertex(int vertex, int edgeIndex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                    "edge at index " + edgeIndex + " references invalid vertex " + vertex
            );
        }
    }

    public static void main(String[] args) {
        int vertexCount = 4;

        int[][] edges = {{0, 1}, {0, 2}, {1, 2}, {2, 3}};

        System.out.println(isBipartite(vertexCount, edges));
    }

    private enum Color {
        UNCOLORED,
        FIRST,
        SECOND;

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
