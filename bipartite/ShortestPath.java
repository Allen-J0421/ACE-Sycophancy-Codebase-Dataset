package bipartite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Computes a shortest path between two vertices of a {@link WeightedUndirectedGraph}
 * using Dijkstra's algorithm: it repeatedly settles the nearest unsettled vertex
 * and relaxes its outgoing edges, backed by a binary-heap priority queue.
 *
 * <p>Dijkstra's algorithm requires non-negative edge weights; because
 * {@link WeightedEdge} permits negative weights, this fails fast with an
 * {@link IllegalArgumentException} if it encounters one while searching. The
 * search settles the target as soon as it is reached, so it does no more work
 * than necessary. It runs in {@code O((V + E) log V)} time and {@code O(V)} space.
 *
 * <p>Shortest paths need edge weights, which the {@link Graph} interface does not
 * expose, so this operates on a {@link WeightedUndirectedGraph} directly.
 */
public final class ShortestPath {

    private static final int NO_PREDECESSOR = -1;

    /** A vertex paired with its tentative distance, ordered by distance in the queue. */
    private record Step(int vertex, double distance) {
    }

    /**
     * Computes a shortest path from {@code source} to {@code target}.
     *
     * @param graph  the weighted graph to search
     * @param source the start vertex, in {@code [0, graph.order())}
     * @param target the destination vertex, in {@code [0, graph.order())}
     * @return a {@link ShortestPathResult.Path} with the distance and vertex
     *         sequence if the target is reachable, otherwise a
     *         {@link ShortestPathResult.Unreachable}
     * @throws NullPointerException      if {@code graph} is null
     * @throws IndexOutOfBoundsException if {@code source} or {@code target} is out of range
     * @throws IllegalArgumentException  if a negative edge weight is encountered
     */
    public ShortestPathResult compute(WeightedUndirectedGraph graph, int source, int target) {
        if (graph == null) {
            throw new NullPointerException("graph must not be null");
        }
        requireVertex(graph, source, "source");
        requireVertex(graph, target, "target");

        int order = graph.order();
        double[] distance = new double[order];
        int[] predecessor = new int[order];
        boolean[] settled = new boolean[order];
        Arrays.fill(distance, Double.POSITIVE_INFINITY);
        Arrays.fill(predecessor, NO_PREDECESSOR);
        distance[source] = 0.0;

        PriorityQueue<Step> frontier = new PriorityQueue<>(Comparator.comparingDouble(Step::distance));
        frontier.add(new Step(source, 0.0));

        while (!frontier.isEmpty()) {
            int u = frontier.poll().vertex();
            if (settled[u]) {
                continue;  // a stale queue entry superseded by a shorter one
            }
            settled[u] = true;
            if (u == target) {
                break;  // the target's distance is now final
            }
            relaxNeighbors(graph, u, distance, predecessor, settled, frontier);
        }

        if (distance[target] == Double.POSITIVE_INFINITY) {
            return new ShortestPathResult.Unreachable(source, target);
        }
        return new ShortestPathResult.Path(distance[target], reconstructPath(predecessor, target));
    }

    private static void relaxNeighbors(WeightedUndirectedGraph graph, int u, double[] distance,
                                       int[] predecessor, boolean[] settled,
                                       PriorityQueue<Step> frontier) {
        for (WeightedEdge edge : graph.weightedNeighbors(u)) {
            double weight = edge.weight();
            if (weight < 0) {
                throw new IllegalArgumentException(
                        "Dijkstra's algorithm requires non-negative weights; found " + weight
                                + " on edge (" + edge.u() + ", " + edge.v() + ")");
            }
            int v = edge.v();
            if (settled[v]) {
                continue;
            }
            double candidate = distance[u] + weight;
            if (candidate < distance[v]) {
                distance[v] = candidate;
                predecessor[v] = u;
                frontier.add(new Step(v, candidate));
            }
        }
    }

    /** Walks predecessors back from {@code target}, yielding the path source-to-target. */
    private static List<Integer> reconstructPath(int[] predecessor, int target) {
        List<Integer> path = new ArrayList<>();
        for (int at = target; at != NO_PREDECESSOR; at = predecessor[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    private static void requireVertex(WeightedUndirectedGraph graph, int vertex, String role) {
        if (vertex < 0 || vertex >= graph.order()) {
            throw new IndexOutOfBoundsException(
                    role + " vertex " + vertex + " is outside the range [0, " + graph.order() + ")");
        }
    }
}
