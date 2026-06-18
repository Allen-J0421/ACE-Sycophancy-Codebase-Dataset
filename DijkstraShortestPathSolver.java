import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

class DijkstraShortestPathSolver {
    private static final int INFINITY = Integer.MAX_VALUE;

    ShortestPathResult solve(WeightedGraphView graph, int sourceNode) {
        Objects.requireNonNull(graph, "Graph cannot be null");
        validateSourceNode(graph, sourceNode);

        DistanceTable distanceTable = DistanceTable.create(graph.getVertexCount(), sourceNode);
        int[] predecessors = initializePredecessors(graph.getVertexCount());
        PriorityQueue<PriorityQueueEntry> priorityQueue = new PriorityQueue<>();
        priorityQueue.offer(PriorityQueueEntry.of(0, sourceNode));

        processQueue(graph, distanceTable, predecessors, priorityQueue);

        List<Integer> distances = convertToList(distanceTable.toArray());
        return ShortestPathResult.of(distances, predecessors, sourceNode, graph);
    }

    private void processQueue(WeightedGraphView graph, DistanceTable distanceTable,
                              int[] predecessors, PriorityQueue<PriorityQueueEntry> priorityQueue) {
        while (!priorityQueue.isEmpty()) {
            processQueueEntry(graph, distanceTable, predecessors, priorityQueue);
        }
    }

    private void processQueueEntry(WeightedGraphView graph, DistanceTable distanceTable,
                                   int[] predecessors, PriorityQueue<PriorityQueueEntry> priorityQueue) {
        PriorityQueueEntry current = priorityQueue.poll();
        int currentNode = current.getNode();
        int currentDistance = current.getDistance();

        if (isOutdatedEntry(currentDistance, distanceTable.getDistance(currentNode))) {
            return;
        }

        graph.getAdjacencyListFor(currentNode).forEach(
            edge -> relaxEdge(distanceTable, predecessors, currentNode, edge, priorityQueue)
        );
    }

    private boolean isOutdatedEntry(int entryDistance, int currentDistance) {
        return entryDistance > currentDistance;
    }

    private void relaxEdge(DistanceTable distanceTable, int[] predecessors, int currentNode,
                          Edge edge, PriorityQueue<PriorityQueueEntry> priorityQueue) {
        int neighbor = edge.getDestination();
        int weight = edge.getWeight();
        int newDistance = distanceTable.getDistance(currentNode) + weight;

        if (newDistance < distanceTable.getDistance(neighbor)) {
            distanceTable.updateDistance(neighbor, newDistance);
            predecessors[neighbor] = currentNode;
            priorityQueue.offer(PriorityQueueEntry.of(newDistance, neighbor));
        }
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
}
