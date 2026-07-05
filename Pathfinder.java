import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.IntStream;

class Pathfinder {
    private record Node(int dist, int vertex) {}

    private static class DistanceTracker {
        static final int UNREACHABLE = PathfindingResult.UNREACHABLE;

        private final Map<Integer, Integer> distances = new HashMap<>();

        void set(int vertex, int distance) {
            distances.put(vertex, distance);
        }

        int get(int vertex) {
            return distances.getOrDefault(vertex, UNREACHABLE);
        }

        boolean isImprovement(int vertex, int candidate) {
            return candidate < get(vertex);
        }

        PathfindingResult toResult(int size) {
            return new PathfindingResult(
                    IntStream.range(0, size).mapToObj(this::get).toList());
        }
    }

    static PathfindingResult dijkstra(Graph graph, int src) {
        int V = graph.size();

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(Node::dist));
        DistanceTracker dist = new DistanceTracker();

        dist.set(src, 0);
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

                if (dist.isImprovement(v, newDist)) {
                    dist.set(v, newDist);
                    pq.offer(new Node(newDist, v));
                }
            }
        }

        return dist.toResult(V);
    }
}
