import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;

final class BipartiteChecker {

    private static final int UNCOLORED = -1;
    private static final int FIRST_COLOR = 0;
    private static final int SECOND_COLOR = 1;

    private BipartiteChecker() {
    }

    static boolean isBipartite(Graph graph) {
        Objects.requireNonNull(graph, "graph");

        int vertexCount = graph.vertexCount();
        int[] colors = new int[vertexCount];
        Arrays.fill(colors, UNCOLORED);

        for (int startVertex = 0; startVertex < vertexCount; startVertex++) {
            if (colors[startVertex] == UNCOLORED && !colorComponent(startVertex, graph, colors)) {
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
}
