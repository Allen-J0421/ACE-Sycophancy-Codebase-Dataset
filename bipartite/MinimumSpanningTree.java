package bipartite;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Computes a minimum spanning tree (MST) of a {@link WeightedUndirectedGraph}
 * using Prim's algorithm: starting from a single vertex, it repeatedly adds the
 * cheapest edge that connects a new vertex to the growing tree, selecting that
 * edge with a binary-heap priority queue.
 *
 * <p>Unlike Dijkstra's algorithm, Prim's is correct with negative edge weights,
 * so no weight restriction applies. If the graph is not connected the tree cannot
 * span it; that case is surfaced as
 * {@link MinimumSpanningTreeResult.Disconnected}. The algorithm runs in
 * {@code O((V + E) log V)} time and {@code O(V + E)} space.
 *
 * <p>Minimum spanning trees need edge weights, which the {@link Graph} interface
 * does not expose, so this operates on a {@link WeightedUndirectedGraph} directly.
 */
public final class MinimumSpanningTree {

    private static final int START_VERTEX = 0;

    /**
     * Computes a minimum spanning tree of the given graph.
     *
     * @param graph the weighted graph to span
     * @return a {@link MinimumSpanningTreeResult.Tree} if the graph is connected,
     *         otherwise a {@link MinimumSpanningTreeResult.Disconnected}
     * @throws NullPointerException if {@code graph} is null
     */
    public MinimumSpanningTreeResult compute(WeightedUndirectedGraph graph) {
        if (graph == null) {
            throw new NullPointerException("graph must not be null");
        }

        int order = graph.order();
        if (order == 0) {
            // A graph with no vertices is trivially spanned by the empty tree.
            return new MinimumSpanningTreeResult.Tree(0.0, List.of());
        }

        boolean[] inTree = new boolean[order];
        PriorityQueue<WeightedEdge> frontier =
                new PriorityQueue<>(Comparator.comparingDouble(WeightedEdge::weight));
        List<WeightedEdge> treeEdges = new ArrayList<>(order - 1);
        double totalWeight = 0.0;

        offerCrossingEdges(graph, START_VERTEX, inTree, frontier);
        int reached = 1;

        while (!frontier.isEmpty() && reached < order) {
            WeightedEdge edge = frontier.poll();
            int next = edge.v();
            if (inTree[next]) {
                continue;  // a stale crossing edge whose endpoint is already covered
            }
            treeEdges.add(edge);
            totalWeight += edge.weight();
            reached++;
            offerCrossingEdges(graph, next, inTree, frontier);
        }

        if (reached < order) {
            return new MinimumSpanningTreeResult.Disconnected(reachableVertices(inTree), order);
        }
        return new MinimumSpanningTreeResult.Tree(totalWeight, treeEdges);
    }

    /** Marks {@code vertex} as part of the tree and offers its edges to not-yet-covered vertices. */
    private static void offerCrossingEdges(WeightedUndirectedGraph graph, int vertex,
                                           boolean[] inTree, PriorityQueue<WeightedEdge> frontier) {
        inTree[vertex] = true;
        for (WeightedEdge edge : graph.weightedNeighbors(vertex)) {
            if (!inTree[edge.v()]) {
                frontier.add(edge);
            }
        }
    }

    private static List<Integer> reachableVertices(boolean[] inTree) {
        List<Integer> reachable = new ArrayList<>();
        for (int vertex = 0; vertex < inTree.length; vertex++) {
            if (inTree[vertex]) {
                reachable.add(vertex);
            }
        }
        return reachable;
    }
}
