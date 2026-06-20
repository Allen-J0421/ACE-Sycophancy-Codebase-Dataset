import java.util.ArrayDeque;
import java.util.Deque;

public final class BipartiteGraph {
    private BipartiteGraph() {
        // Utility class.
    }

    public static boolean isBipartite(int vertexCount, int[][] edges) {
        return isBipartite(Graph.fromEdges(vertexCount, edges));
    }

    public static boolean isBipartite(Graph graph) {
        ColoringState coloring = new ColoringState(graph.vertexCount());

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!coloring.isColored(vertex)
                    && !isComponentBipartite(vertex, graph, coloring)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isComponentBipartite(
            int startVertex,
            Graph graph,
            ColoringState coloring) {
        Deque<Integer> queue = new ArrayDeque<>();
        coloring.colorStartVertex(startVertex);
        queue.offer(startVertex);

        while (!queue.isEmpty()) {
            int currentVertex = queue.poll();

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (!coloring.isColored(neighbor)) {
                    coloring.colorWithOpposite(neighbor, currentVertex);
                    queue.offer(neighbor);
                    continue;
                }

                if (coloring.hasSameColor(currentVertex, neighbor)) {
                    return false;
                }
            }
        }

        return true;
    }

    private enum VertexColor {
        FIRST,
        SECOND;

        VertexColor opposite() {
            return this == FIRST ? SECOND : FIRST;
        }
    }

    private static final class ColoringState {
        private final VertexColor[] colors;

        private ColoringState(int vertexCount) {
            this.colors = new VertexColor[vertexCount];
        }

        private boolean isColored(int vertex) {
            return colors[vertex] != null;
        }

        private void colorStartVertex(int vertex) {
            colors[vertex] = VertexColor.FIRST;
        }

        private void colorWithOpposite(int vertex, int referenceVertex) {
            colors[vertex] = colors[referenceVertex].opposite();
        }

        private boolean hasSameColor(int firstVertex, int secondVertex) {
            return colors[firstVertex] == colors[secondVertex];
        }
    }
}
