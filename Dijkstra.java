import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

class Dijkstra {
    private Dijkstra() {
    }

    private static final class NodeDistance {
        private final int node;
        private final int distance;

        private NodeDistance(int node, int distance) {
            this.node = node;
            this.distance = distance;
        }
    }

    static ArrayList<Integer> dijkstra(ArrayList<ArrayList<int[]>> adjacency, int source) {
        return new ArrayList<>(shortestPathsFrom(LegacyGraphAdapter.toWeightedGraph(adjacency), source));
    }

    static ArrayList<Integer> dijkstra(WeightedGraph graph, int source) {
        return new ArrayList<>(shortestPathsFrom(graph, source));
    }

    static List<Integer> shortestPathsFrom(WeightedGraph graph, int source) {
        validateSource(graph, source);

        DistanceTable distances = DistanceTable.withSource(graph.vertexCount(), source);
        PriorityQueue<NodeDistance> queue =
                new PriorityQueue<>((a, b) -> Integer.compare(a.distance, b.distance));

        queue.offer(new NodeDistance(source, 0));

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();

            if (distances.hasShorterPathTo(current.node, current.distance)) {
                continue;
            }

            relaxEdges(graph, current, distances, queue);
        }

        return distances.toList();
    }

    static void addEdge(ArrayList<ArrayList<int[]>> adjacency, int u, int v, int weight) {
        LegacyGraphAdapter.addUndirectedEdge(adjacency, u, v, weight);
    }

    private static void validateSource(WeightedGraph graph, int source) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null.");
        }
        if (!graph.hasVertex(source)) {
            throw new IllegalArgumentException("Source vertex is outside the graph.");
        }
    }

    private static void relaxEdges(
            WeightedGraph graph,
            NodeDistance current,
            DistanceTable distances,
            PriorityQueue<NodeDistance> queue) {
        for (Edge edge : graph.edgesFrom(current.node)) {
            if (distances.tryRelax(current.node, edge)) {
                queue.offer(new NodeDistance(edge.destination(), distances.distanceTo(edge.destination())));
            }
        }
    }

    public static void main(String[] args) {
        List<Integer> result = shortestPathsFrom(SampleGraphs.demoGraph(), 0);
        for (int distance : result) {
            System.out.print(distance + " ");
        }
        System.out.println();
    }
}
