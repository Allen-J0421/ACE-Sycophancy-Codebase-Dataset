import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

class Dijkstra {

    record Edge(int to, int weight) {}

    record Node(int dist, int vertex) {}

    static ArrayList<Integer> dijkstra(ArrayList<ArrayList<Edge>> adj, int src) {
        int V = adj.size();

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

            for (Edge edge : adj.get(u)) {
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

    static void addEdge(ArrayList<ArrayList<Edge>> adj, int u, int v, int w) {
        adj.get(u).add(new Edge(v, w));
        adj.get(v).add(new Edge(u, w));
    }

    public static void main(String[] args) {
        int V = 5;
        int src = 0;

        ArrayList<ArrayList<Edge>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }

        addEdge(adj, 0, 1, 4);
        addEdge(adj, 0, 2, 8);
        addEdge(adj, 1, 4, 6);
        addEdge(adj, 1, 2, 3);
        addEdge(adj, 2, 3, 2);
        addEdge(adj, 3, 4, 10);

        ArrayList<Integer> result = dijkstra(adj, src);
        for (int d : result)
            System.out.print(d + " ");
        System.out.println();
    }
}
