import java.util.Comparator;
import java.util.PriorityQueue;

final class ShortestPathSolver {
    private final Graph graph;
    private final Distances distances;
    private final PriorityQueue<QueueEntry> minHeap;

    private ShortestPathSolver(Graph graph, int source) {
        if (graph == null) {
            throw new IllegalArgumentException("graph must not be null");
        }

        this.graph = graph;
        this.distances = Distances.forSource(graph.vertexCount(), source);
        this.minHeap = new PriorityQueue<>(Comparator.comparingInt(QueueEntry::distance));
        this.minHeap.offer(new QueueEntry(source, 0));
    }

    static Distances solve(Graph graph, int source) {
        return new ShortestPathSolver(graph, source).run();
    }

    private Distances run() {
        while (!minHeap.isEmpty()) {
            QueueEntry current = minHeap.poll();
            if (distances.isStale(current)) {
                continue;
            }

            relaxNeighborsOf(current);
        }
        return distances;
    }

    private void relaxNeighborsOf(QueueEntry current) {
        for (Edge edge : graph.neighborsOf(current.vertex())) {
            tryRelax(current, edge);
        }
    }

    private void tryRelax(QueueEntry current, Edge edge) {
        if (current.distance() == Integer.MAX_VALUE) {
            return;
        }

        long candidateDistance = (long) current.distance() + edge.weight();
        if (candidateDistance > Integer.MAX_VALUE) {
            return;
        }

        int nextDistance = (int) candidateDistance;
        if (distances.tryUpdate(edge.to(), nextDistance)) {
            minHeap.offer(new QueueEntry(edge.to(), nextDistance));
        }
    }
}
