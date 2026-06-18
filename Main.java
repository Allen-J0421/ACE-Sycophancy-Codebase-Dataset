import java.util.List;

public class Main {
    public static void main(String[] args) {
        UndirectedGraph graph = new UndirectedGraph(6);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(0, 3);
        graph.addEdge(4, 5);

        BreadthFirstSearch bfs = new BreadthFirstSearch(graph);
        List<Integer> traversalOrder = bfs.traverse();

        System.out.print("BFS Traversal: ");
        for (int vertex : traversalOrder) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
