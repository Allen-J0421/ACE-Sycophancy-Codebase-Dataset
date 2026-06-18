import java.util.List;

class BreadthFirstSearch {
    public static void main(String[] args) {
        Graph graph = buildSampleGraph();
        List<Integer> traversalOrder = BreadthFirstTraversal.traverseAllComponents(graph);
        printTraversal(traversalOrder);
    }

    private static Graph buildSampleGraph() {
        AdjacencyListGraph graph = new AdjacencyListGraph(6);
        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(2, 0);
        graph.addUndirectedEdge(0, 3);
        graph.addUndirectedEdge(4, 5);
        return graph;
    }

    private static void printTraversal(List<Integer> traversalOrder) {
        for (int vertex : traversalOrder) {
            System.out.print(vertex + " ");
        }
    }
}
