import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class BipartiteChecker {

    enum Color {
        RED, BLUE;

        Color opposite() {
            return this == RED ? BLUE : RED;
        }
    }

    static class Coloring {
        private final Color[] colors;

        Coloring(int V) {
            colors = new Color[V];
        }

        void set(int v, Color c) {
            colors[v] = c;
        }

        Color get(int v) {
            return colors[v];
        }

        boolean isUncolored(int v) {
            return colors[v] == null;
        }
    }

    static class Partition {
        final List<Integer> setA = new ArrayList<>();
        final List<Integer> setB = new ArrayList<>();
        final boolean bipartite;

        Partition(boolean bipartite) {
            this.bipartite = bipartite;
        }
    }

    static Partition check(Graph graph) {
        int V = graph.vertexCount();
        Coloring coloring = new Coloring(V);

        for (int i = 0; i < V; i++) {
            if (coloring.isUncolored(i)) {
                if (!bfsColor(graph, i, coloring)) {
                    return new Partition(false);
                }
            }
        }

        Partition result = new Partition(true);
        for (int i = 0; i < V; i++) {
            if (coloring.get(i) == Color.RED) {
                result.setA.add(i);
            } else {
                result.setB.add(i);
            }
        }
        return result;
    }

    private static boolean bfsColor(Graph graph, int start, Coloring coloring) {
        Queue<Integer> queue = new LinkedList<>();
        coloring.set(start, Color.RED);
        queue.offer(start);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : graph.neighbors(u)) {
                if (coloring.isUncolored(v)) {
                    coloring.set(v, coloring.get(u).opposite());
                    queue.offer(v);
                } else if (coloring.get(v) == coloring.get(u)) {
                    return false;
                }
            }
        }
        return true;
    }
}
