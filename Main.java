public class Main {
    public static void main(String[] args) {
        System.out.println("=== Undirected Graph with BFS ===");
        Graph undirectedGraph = GraphBuilder.undirected(6)
                .addEdge(1, 2)
                .addEdge(2, 0)
                .addEdge(0, 3)
                .addEdge(4, 5)
                .build();

        GraphTraversal bfs = new BreadthFirstSearch();
        TraversalResult bfsResult = bfs.traverse(undirectedGraph);
        bfsResult.print();

        System.out.println("\n=== Undirected Graph with DFS ===");
        GraphTraversal dfs = new DepthFirstSearch();
        TraversalResult dfsResult = dfs.traverse(undirectedGraph);
        dfsResult.print();

        System.out.println("\n=== Directed Graph with BFS ===");
        Graph directedGraph = GraphBuilder.directed(5)
                .addEdge(0, 1)
                .addEdge(0, 2)
                .addEdge(1, 3)
                .addEdge(2, 4)
                .build();

        TraversalResult directedBfsResult = bfs.traverse(directedGraph);
        directedBfsResult.print();
    }
}
