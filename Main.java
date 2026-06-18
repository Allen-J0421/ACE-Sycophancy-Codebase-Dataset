import java.util.List;

class Main {
    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(0, 3);
        graph.addEdge(4, 5);

        List<Integer> result = BreadthFirstSearch.traverse(graph);
        for (int vertex : result) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
