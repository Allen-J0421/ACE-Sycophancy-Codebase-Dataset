import java.util.List;

class Dijkstra {
    public static void main(String[] args) {
        int src = 0;

        Graph graph = new Graph(5)
                .addEdge(0, 1, 4)
                .addEdge(0, 2, 8)
                .addEdge(1, 4, 6)
                .addEdge(1, 2, 3)
                .addEdge(2, 3, 2)
                .addEdge(3, 4, 10);

        List<Integer> result = Pathfinder.dijkstra(graph, src);
        for (int d : result)
            System.out.print(d + " ");
        System.out.println();
    }
}
