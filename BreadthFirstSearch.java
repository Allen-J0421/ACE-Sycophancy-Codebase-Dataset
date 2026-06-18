public class BreadthFirstSearch {
    public static void main(String[] args) {
        UndirectedGraph graph = SampleGraphFactory.create();
        System.out.print(TraversalFormatter.format(BreadthFirstTraversal.traverse(graph)));
    }
}
