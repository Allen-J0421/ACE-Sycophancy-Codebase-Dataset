import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

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

        return Arrays.stream(dist)
                .boxed()
                .toList();
    }
}
