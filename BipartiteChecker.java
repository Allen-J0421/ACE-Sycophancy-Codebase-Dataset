import java.util.ArrayList;
import java.util.List;

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

        void accept(PartitionVisitor visitor) {
            if (bipartite) {
                visitor.visitBipartite(setA, setB);
            } else {
                visitor.visitNonBipartite();
            }
        }
    }

    private final ColoringStrategy strategy;

    BipartiteChecker(ColoringStrategy strategy) {
        this.strategy = strategy;
    }

    Partition check(Graph graph) {
        int V = graph.vertexCount();
        Coloring coloring = new Coloring(V);

        for (int i = 0; i < V; i++) {
            if (coloring.isUncolored(i)) {
                if (!strategy.colorComponent(graph, i, coloring)) {
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
}
