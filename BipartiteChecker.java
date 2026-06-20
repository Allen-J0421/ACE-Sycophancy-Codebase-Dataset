import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

final class BipartiteChecker {

    private BipartiteChecker() {
    }

    static boolean isBipartite(Graph graph) {
        Objects.requireNonNull(graph, "graph");

        Coloring coloring = new Coloring(graph.vertexCount());

        for (int startVertex = 0; startVertex < coloring.size(); startVertex++) {
            if (coloring.isUncolored(startVertex) && !colorComponent(startVertex, graph, coloring)) {
                return false;
            }
        }

        return true;
    }

    private static boolean colorComponent(int startVertex, Graph graph, Coloring coloring) {
        Deque<Integer> queue = new ArrayDeque<>();
        coloring.assign(startVertex, Color.LEFT);
        queue.addLast(startVertex);

        while (!queue.isEmpty()) {
            int currentVertex = queue.removeFirst();
            Color currentColor = coloring.colorOf(currentVertex);
            Color oppositeColor = currentColor.opposite();

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (coloring.isUncolored(neighbor)) {
                    coloring.assign(neighbor, oppositeColor);
                    queue.addLast(neighbor);
                    continue;
                }

                if (coloring.colorOf(neighbor) == currentColor) {
                    return false;
                }
            }
        }

        return true;
    }

    private enum Color {
        LEFT,
        RIGHT;

        Color opposite() {
            return this == LEFT ? RIGHT : LEFT;
        }
    }

    private static final class Coloring {
        private final Color[] colors;

        private Coloring(int vertexCount) {
            this.colors = new Color[vertexCount];
        }

        int size() {
            return colors.length;
        }

        boolean isUncolored(int vertex) {
            return colors[vertex] == null;
        }

        Color colorOf(int vertex) {
            return colors[vertex];
        }

        void assign(int vertex, Color color) {
            colors[vertex] = color;
        }
    }
}
