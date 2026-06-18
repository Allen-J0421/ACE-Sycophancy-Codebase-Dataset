import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Dijkstra's shortest-path algorithm for a weighted, undirected graph with
 * non-negative edge weights.
 *
 * <p>Given a source vertex, computes the minimum distance to every other
 * vertex in O((V + E) log V) time using a binary-heap priority queue.
 */
class Dijkstra {

    /** Distance assigned to vertices that cannot be reached from the source. */
    static final int UNREACHABLE = Integer.MAX_VALUE;

    /** A weighted edge pointing at a neighbouring vertex. */
    record Edge(int to, int weight) {}

    /** A weighted, undirected graph backed by adjacency lists. */
    static final class Graph {
        private final List<List<Edge>> adjacency;

        Graph(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must be non-negative");
            }
            adjacency = new ArrayList<>(vertexCount);
            for (int i = 0; i < vertexCount; i++) {
                adjacency.add(new ArrayList<>());
            }
        }

        int vertexCount() {
            return adjacency.size();
        }

        List<Edge> neighbours(int vertex) {
            return adjacency.get(vertex);
        }

        /** Adds an undirected edge of the given weight between {@code u} and {@code v}. */
        void addEdge(int u, int v, int weight) {
            if (weight < 0) {
                throw new IllegalArgumentException(
                        "Dijkstra requires non-negative weights, got " + weight);
            }
            adjacency.get(u).add(new Edge(v, weight));
            adjacency.get(v).add(new Edge(u, weight));
        }
    }

    /** A vertex paired with its current tentative distance, ordered by distance. */
    private record State(int vertex, int distance) {}

    /**
     * Computes the shortest distance from {@code source} to every vertex.
     *
     * @return an array where index {@code i} holds the distance from
     *         {@code source} to vertex {@code i}, or {@link #UNREACHABLE}
     *         when no path exists.
     */
    static int[] shortestDistances(Graph graph, int source) {
        int[] dist = new int[graph.vertexCount()];
        Arrays.fill(dist, UNREACHABLE);
        dist[source] = 0;

        PriorityQueue<State> queue =
                new PriorityQueue<>(Comparator.comparingInt(State::distance));
        queue.offer(new State(source, 0));

        while (!queue.isEmpty()) {
            State current = queue.poll();

            // Skip stale entries left in the queue after a shorter path was found.
            if (current.distance() > dist[current.vertex()]) {
                continue;
            }

            for (Edge edge : graph.neighbours(current.vertex())) {
                int candidate = dist[current.vertex()] + edge.weight();
                if (candidate < dist[edge.to()]) {
                    dist[edge.to()] = candidate;
                    queue.offer(new State(edge.to(), candidate));
                }
            }
        }

        return dist;
    }

    public static void main(String[] args) {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 8);
        graph.addEdge(1, 4, 6);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, 2);
        graph.addEdge(3, 4, 10);

        int[] distances = shortestDistances(graph, 0);

        StringBuilder line = new StringBuilder();
        for (int d : distances) {
            line.append(d).append(' ');
        }
        System.out.println(line.toString().trim());
    }
}
