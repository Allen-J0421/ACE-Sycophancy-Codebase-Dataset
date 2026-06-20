import java.util.ArrayDeque;
import java.util.Deque;

public final class BipartiteGraph {
    private BipartiteGraph() {
        // Utility class.
    }

    static boolean isBipartite(int vertexCount, int[][] edges) {
        return isBipartite(Graph.fromEdges(vertexCount, edges));
    }

    static boolean isBipartite(Graph graph) {
        VertexColor[] colors = new VertexColor[graph.vertexCount()];

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (colors[vertex] == null
                    && !isComponentBipartite(vertex, graph, colors)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isComponentBipartite(
            int startVertex,
            Graph graph,
            VertexColor[] colors) {
        Deque<Integer> queue = new ArrayDeque<>();
        colors[startVertex] = VertexColor.FIRST;
        queue.offer(startVertex);

        while (!queue.isEmpty()) {
            int currentVertex = queue.poll();

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (colors[neighbor] == null) {
                    colors[neighbor] = colors[currentVertex].opposite();
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

    public static void main(String[] args) {
        int vertexCount = 4;
        int[][] edges = {{0, 1}, {0, 2}, {1, 2}, {2, 3}};

        System.out.println(isBipartite(vertexCount, edges));
    }

    private enum VertexColor {
        FIRST,
        SECOND;

        VertexColor opposite() {
            return this == FIRST ? SECOND : FIRST;
        }
    }
}
