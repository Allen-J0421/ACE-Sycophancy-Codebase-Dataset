import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

class Dijkstra {
    private static final int INFINITE_DISTANCE = Integer.MAX_VALUE;
    private static final int EDGE_DESTINATION_INDEX = 0;
    private static final int EDGE_WEIGHT_INDEX = 1;

    static final class Graph {
        private final ArrayList<ArrayList<Edge>> adjacency;

        private Graph(int vertexCount) {
            validateVertexCount(vertexCount);

            adjacency = new ArrayList<>();
            for (int i = 0; i < vertexCount; i++) {
                adjacency.add(new ArrayList<>());
            }
        }

        static Graph withVertexCount(int vertexCount) {
            return new Graph(vertexCount);
        }

        void addUndirectedEdge(int u, int v, int weight) {
            validateVertex(u);
            validateVertex(v);
            validateWeight(weight);

            addDirectedEdge(u, v, weight);
            addDirectedEdge(v, u, weight);
        }

        int vertexCount() {
            return adjacency.size();
        }

        List<Edge> edgesFrom(int vertex) {
            validateVertex(vertex);
            return Collections.unmodifiableList(adjacency.get(vertex));
        }

        private void addDirectedEdge(int from, int to, int weight) {
            validateVertex(from);
            validateVertex(to);
            validateWeight(weight);

            adjacency.get(from).add(new Edge(to, weight));
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= adjacency.size()) {
                throw new IllegalArgumentException("Vertex is outside the graph.");
            }
        }
    }

    private static final class Edge {
        private final int destination;
        private final int weight;

        private Edge(int destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    private static final class NodeDistance {
        private final int node;
        private final int distance;

        private NodeDistance(int node, int distance) {
            this.node = node;
            this.distance = distance;
        }
    }

    static ArrayList<Integer> dijkstra(ArrayList<ArrayList<int[]>> adj, int src) {
        return dijkstra(fromLegacyAdjacencyList(adj), src);
    }

    static ArrayList<Integer> dijkstra(Graph graph, int src) {
        validateGraph(graph, src);

        int vertexCount = graph.vertexCount();
        PriorityQueue<NodeDistance> pq =
                new PriorityQueue<>((a, b) -> Integer.compare(a.distance, b.distance));

        int[] dist = new int[vertexCount];
        Arrays.fill(dist, INFINITE_DISTANCE);

        dist[src] = 0;
        pq.offer(new NodeDistance(src, 0));

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();

            if (current.distance > dist[current.node]) {
                continue;
            }

            for (Edge edge : graph.edgesFrom(current.node)) {
                if (current.distance > INFINITE_DISTANCE - edge.weight) {
                    continue;
                }

                int candidateDistance = current.distance + edge.weight;
                if (candidateDistance < dist[edge.destination]) {
                    dist[edge.destination] = candidateDistance;
                    pq.offer(new NodeDistance(edge.destination, candidateDistance));
                }
            }
        }

        return toList(dist);
    }

    private static void validateGraph(Graph graph, int src) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null.");
        }
        if (src < 0 || src >= graph.vertexCount()) {
            throw new IllegalArgumentException("Source vertex is outside the graph.");
        }
    }

    private static Graph fromLegacyAdjacencyList(ArrayList<ArrayList<int[]>> adj) {
        if (adj == null) {
            throw new IllegalArgumentException("Graph cannot be null.");
        }

        Graph graph = Graph.withVertexCount(adj.size());
        for (int u = 0; u < adj.size(); u++) {
            List<int[]> edges = adj.get(u);
            if (edges == null) {
                throw new IllegalArgumentException("Adjacency list cannot contain null vertex entries.");
            }
            for (int[] edge : edges) {
                validateEdge(adj.size(), edge);
                graph.addDirectedEdge(u, destination(edge), weight(edge));
            }
        }

        return graph;
    }

    private static void validateEdge(int vertexCount, int[] edge) {
        if (edge == null || edge.length < 2) {
            throw new IllegalArgumentException("Each edge must contain a destination and weight.");
        }
        if (destination(edge) < 0 || destination(edge) >= vertexCount) {
            throw new IllegalArgumentException("Edge destination is outside the graph.");
        }
        validateWeight(weight(edge));
    }

    private static int destination(int[] edge) {
        return edge[EDGE_DESTINATION_INDEX];
    }

    private static int weight(int[] edge) {
        return edge[EDGE_WEIGHT_INDEX];
    }

    private static ArrayList<Integer> toList(int[] distances) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int distance : distances) {
            result.add(distance);
        }
        return result;
    }

    static void addEdge(ArrayList<ArrayList<int[]>> adj, int u, int v, int w) {
        validateLegacyVertex(adj, u);
        validateLegacyVertex(adj, v);
        validateWeight(w);

        adj.get(u).add(new int[]{v, w});
        adj.get(v).add(new int[]{u, w});
    }

    private static void validateLegacyVertex(ArrayList<ArrayList<int[]>> adj, int vertex) {
        if (adj == null) {
            throw new IllegalArgumentException("Graph cannot be null.");
        }
        if (vertex < 0 || vertex >= adj.size()) {
            throw new IllegalArgumentException("Vertex is outside the graph.");
        }
        if (adj.get(vertex) == null) {
            throw new IllegalArgumentException("Adjacency list cannot contain null vertex entries.");
        }
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }
    }

    private static void validateWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Dijkstra's algorithm requires non-negative weights.");
        }
    }

    public static void main(String[] args) {
        int vertexCount = 5;
        int src = 0;

        Graph graph = Graph.withVertexCount(vertexCount);

        addSampleEdges(graph);

        ArrayList<Integer> result = dijkstra(graph, src);
        for (int distance : result) {
            System.out.print(distance + " ");
        }
        System.out.println();
    }

    private static void addSampleEdges(Graph graph) {
        graph.addUndirectedEdge(0, 1, 4);
        graph.addUndirectedEdge(0, 2, 8);
        graph.addUndirectedEdge(1, 4, 6);
        graph.addUndirectedEdge(1, 2, 3);
        graph.addUndirectedEdge(2, 3, 2);
        graph.addUndirectedEdge(3, 4, 10);
    }
}
