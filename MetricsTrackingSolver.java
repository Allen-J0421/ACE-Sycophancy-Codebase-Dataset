import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 * Extended Dijkstra solver that tracks execution metrics.
 * Useful for performance analysis and algorithm validation.
 *
 * Overhead: Minimal, only tracking counts and timestamps
 */
class MetricsTrackingSolver {
    private static final int INFINITY = Integer.MAX_VALUE;
    private final ExecutionMetrics metrics;

    MetricsTrackingSolver() {
        this.metrics = new ExecutionMetrics();
    }

    /**
     * Solves shortest path problem while tracking metrics.
     *
     * @param graph The weighted graph
     * @param sourceNode The source vertex
     * @return Result containing distances, paths, and metrics
     */
    ShortestPathResult solveWithMetrics(WeightedGraphView graph, int sourceNode) {
        Objects.requireNonNull(graph, "Graph cannot be null");
        validateSourceNode(graph, sourceNode);

        metrics.recordStart();

        DistanceTable distanceTable = DistanceTable.create(graph.getVertexCount(), sourceNode);
        int[] predecessors = initializePredecessors(graph.getVertexCount());
        PriorityQueue<PriorityQueueEntry> priorityQueue = new PriorityQueue<>();
        priorityQueue.offer(PriorityQueueEntry.of(0, sourceNode));
        metrics.incrementEnqueueCount();

        processQueueWithMetrics(graph, distanceTable, predecessors, priorityQueue);

        metrics.recordEnd();

        List<Integer> distances = convertToList(distanceTable.toArray());
        return ShortestPathResult.of(distances, predecessors, sourceNode, graph);
    }

    private void processQueueWithMetrics(WeightedGraphView graph, DistanceTable distanceTable,
                                        int[] predecessors, PriorityQueue<PriorityQueueEntry> priorityQueue) {
        while (!priorityQueue.isEmpty()) {
            PriorityQueueEntry current = priorityQueue.poll();
            metrics.incrementDequeueCount();

            int currentNode = current.getNode();
            int currentDistance = current.getDistance();

            if (isOutdatedEntry(currentDistance, distanceTable.getDistance(currentNode))) {
                continue;
            }

            metrics.incrementVerticesProcessed();
            List<Edge> edges = graph.getAdjacencyListFor(currentNode);
            metrics.addEdgesProcessed(edges.size());

            for (Edge edge : edges) {
                int neighbor = edge.getDestination();
                int weight = edge.getWeight();
                int newDistance = distanceTable.getDistance(currentNode) + weight;

                if (newDistance < distanceTable.getDistance(neighbor)) {
                    distanceTable.updateDistance(neighbor, newDistance);
                    predecessors[neighbor] = currentNode;
                    priorityQueue.offer(PriorityQueueEntry.of(newDistance, neighbor));
                    metrics.incrementEnqueueCount();
                    metrics.incrementRelaxationCount();
                }
            }
        }
    }

    private boolean isOutdatedEntry(int entryDistance, int currentDistance) {
        return entryDistance > currentDistance;
    }

    private int[] initializePredecessors(int vertexCount) {
        int[] predecessors = new int[vertexCount];
        Arrays.fill(predecessors, -1);
        return predecessors;
    }

    private List<Integer> convertToList(int[] distances) {
        return new ArrayList<>(Arrays.asList(
            Arrays.stream(distances).boxed().toArray(Integer[]::new)
        ));
    }

    private void validateSourceNode(WeightedGraphView graph, int sourceNode) {
        if (sourceNode < 0 || sourceNode >= graph.getVertexCount()) {
            throw new IllegalArgumentException(
                String.format("Source node %d is invalid for graph with %d vertices",
                             sourceNode, graph.getVertexCount())
            );
        }
    }

    ExecutionMetrics getMetrics() {
        return metrics;
    }
}
