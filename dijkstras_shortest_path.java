import java.util.List;

class Dijkstra {
    public static void main(String[] args) {
        int src = 0;

        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 8);
        graph.addEdge(1, 4, 6);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, 2);
        graph.addEdge(3, 4, 10);

        List<Integer> result = Pathfinder.dijkstra(graph, src);
        for (int d : result)
            System.out.print(d + " ");
        System.out.println();
    }
}
