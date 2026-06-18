public class BreadthFirstSearch {
    public static void main(String[] args) {
        Graph graph = SampleGraphFactory.create();
        GraphTraversal traversal = new BreadthFirstTraversal();
        System.out.print(TraversalFormatter.format(traversal.traverse(graph)));
    }
}
