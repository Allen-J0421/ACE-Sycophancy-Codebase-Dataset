import java.util.List;

class BipartiteAnalyzer {

    static class Coloring {
        private final BipartiteChecker.Coloring internal;

        Coloring(BipartiteChecker.Coloring internal) {
            this.internal = internal;
        }

        BipartiteChecker.Color getColor(int v) {
            return internal.get(v);
        }
    }

    static class Partition {
        private final BipartiteChecker.Partition internal;

        Partition(BipartiteChecker.Partition internal) {
            this.internal = internal;
        }

        boolean isBipartite() {
            return internal.bipartite;
        }

        List<Integer> getSetA() {
            return internal.setA;
        }

        List<Integer> getSetB() {
            return internal.setB;
        }
    }

    public static void main(String[] args) {
        int V = 4;
        int[][] edges = {{0, 1}, {0, 2}, {1, 2}, {2, 3}};

        GraphInputValidator.validate(V, edges);
        Graph graph = UndirectedGraphFactory.fromEdges(V, edges);
        BipartiteChecker.Partition result = BipartiteChecker.check(graph);
        GraphView.printResult(result);
    }
}
