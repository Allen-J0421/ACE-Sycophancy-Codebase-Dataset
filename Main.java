import java.util.List;

public class Main {
    public static void main(String[] args) {
        Graph graph = buildGraph();

        System.out.println("=== DFS (Recursive) ===");
        List<Integer> dfsRecursive = DepthFirstSearch.recursive().traverse(graph);
        printTraversal(dfsRecursive);

        System.out.println("\n=== DFS (Iterative) ===");
        List<Integer> dfsIterative = DepthFirstSearch.iterative().traverse(graph);
        printTraversal(dfsIterative);

        System.out.println("\n=== BFS ===");
        List<Integer> bfs = new BreadthFirstSearch().traverse(graph);
        printTraversal(bfs);

        System.out.println("\n=== Graph Statistics ===");
        System.out.println("Vertices: " + graph.getVertexCount());
        System.out.println("Edges: " + graph.getEdgeCount());
        System.out.println("Degree of vertex 0: " + graph.getDegree(0));
    }

    private static Graph buildGraph() {
        return new GraphBuilder(6)
                .addEdge(1, 2)
                .addEdge(0, 3)
                .addEdge(2, 0)
                .addEdge(5, 4)
                .build();
    }

    private static void printTraversal(List<Integer> traversal) {
        for (int i = 0; i < traversal.size(); i++) {
            System.out.print(traversal.get(i));
            if (i < traversal.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }
}
