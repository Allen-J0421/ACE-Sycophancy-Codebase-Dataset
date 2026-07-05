import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

record Edge(int to, int weight) {}

class Graph {
    private final ArrayList<ArrayList<Edge>> adj;

    Graph(int vertices) {
        adj = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++)
            adj.add(new ArrayList<>());
    }

    void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(v, w));
        adj.get(v).add(new Edge(u, w));
    }

    List<Edge> neighbors(int u) {
        return adj.get(u);
    }

    int size() {
        return adj.size();
    }
}

class Pathfinder {
    private record Node(int dist, int vertex) {}

    static List<Integer> dijkstra(Graph graph, int src) {
        int V = graph.size();

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(Node::dist));

        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);

        dist[src] = 0;
        pq.offer(new Node(0, src));

        while (!pq.isEmpty()) {
            Node top = pq.poll();
            int d = top.dist();
            int u = top.vertex();

            if (d > dist[u])
                continue;

            for (Edge edge : graph.neighbors(u)) {
                int v = edge.to();
                int w = edge.weight();

                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pq.offer(new Node(dist[v], v));
                }
            }
        }

        ArrayList<Integer> result = new ArrayList<>();
        for (int d : dist)
            result.add(d);

        return result;
    }
}

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
