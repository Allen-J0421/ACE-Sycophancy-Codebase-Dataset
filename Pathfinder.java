import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.IntStream;

class Pathfinder {
    private record Node(int dist, int vertex) {}

    static final int UNREACHABLE = Integer.MAX_VALUE;

    static List<Integer> dijkstra(Graph graph, int src) {
        int V = graph.size();

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(Node::dist));
        Map<Integer, Integer> dist = new HashMap<>();

        dist.put(src, 0);
        pq.offer(new Node(0, src));

        while (!pq.isEmpty()) {
            Node top = pq.poll();
            int d = top.dist();
            int u = top.vertex();

            if (d > dist.get(u))
                continue;

            for (Edge edge : graph.neighbors(u)) {
                int v = edge.to();
                int newDist = dist.get(u) + edge.weight();

                if (newDist < dist.getOrDefault(v, UNREACHABLE)) {
                    dist.put(v, newDist);
                    pq.offer(new Node(newDist, v));
                }
            }
        }

        return IntStream.range(0, V)
                .mapToObj(i -> dist.getOrDefault(i, UNREACHABLE))
                .toList();
    }
}
