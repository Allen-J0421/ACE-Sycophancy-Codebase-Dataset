import java.util.Optional;

class BipartiteAnalyzer {

    public static void main(String[] args) {
        int V = 4;
        int[][] edges = {{0, 1}, {0, 2}, {1, 2}, {2, 3}};

        GraphInputValidator.validate(V, edges);
        GraphFactory factory = new UndirectedGraphFactory();
        Graph graph = factory.fromEdges(V, edges);
        BipartiteChecker checker = new BipartiteChecker(new BfsColoringStrategy());
        Optional<Partition> result = checker.check(graph);
        result.ifPresentOrElse(GraphView::printBipartite, GraphView::printNonBipartite);
    }
}
