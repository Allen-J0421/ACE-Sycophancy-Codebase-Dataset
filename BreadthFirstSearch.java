import java.util.List;

class BreadthFirstSearch {
    public static void main(String[] args) {
        Graph graph = SampleGraphs.disconnectedUndirectedGraph();
        List<Integer> traversalOrder = BreadthFirstTraversal.traverseAllComponents(graph);
        printTraversal(traversalOrder);
    }

    private static void printTraversal(List<Integer> traversalOrder) {
        for (int vertex : traversalOrder) {
            System.out.print(vertex + " ");
        }
    }
}
