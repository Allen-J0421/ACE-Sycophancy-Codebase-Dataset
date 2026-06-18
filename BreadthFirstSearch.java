class BreadthFirstSearch {
    public static void main(String[] args) {
        Graph graph = SampleGraphs.disconnectedUndirectedGraph();
        TraversalResult traversalResult = BreadthFirstTraversal.traverseAllComponents(graph);
        System.out.print(traversalResult.formatVisitOrder());
    }
}
