import java.util.List;

public class Main {
    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);

        DepthFirstSearch dfs = new DepthFirstSearch(graph);
        List<Integer> traversalOrder = dfs.traverse();

        System.out.println("DFS Traversal Order:");
        for (int vertex : traversalOrder) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
