package com.algorithms.scc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Kosaraju's two-pass algorithm for strongly connected components.
 *
 * <ol>
 *   <li>Depth-first search over the original graph, recording vertices in order
 *       of completion ("finish order").</li>
 *   <li>Depth-first search over the {@linkplain DirectedGraph#transpose()
 *       transpose}, visiting vertices in reverse finish order. Each tree of the
 *       second search is one strongly connected component.</li>
 * </ol>
 *
 * <p>Both passes are implemented <em>iteratively</em> with an explicit stack.
 * The original implementation recursed, which overflows the call stack on deep
 * graphs; this version runs in {@code O(V + E)} time and {@code O(V)} auxiliary
 * space regardless of graph depth.
 *
 * @param <V> the vertex label type
 */
public final class KosarajuStronglyConnectedComponentsFinder<V>
        implements StronglyConnectedComponentsFinder<V> {

    @Override
    public StronglyConnectedComponentsResult<V> find(DirectedGraph<V> graph) {
        List<V> finishOrder = computeFinishOrder(graph);
        return collectComponents(graph.transpose(), finishOrder);
    }

    /**
     * First pass: iterative post-order DFS over the original graph. A vertex is
     * appended to the result once all of its descendants have been processed,
     * yielding vertices in ascending order of completion time.
     */
    private List<V> computeFinishOrder(DirectedGraph<V> graph) {
        List<V> finishOrder = new ArrayList<>(graph.vertexCount());
        Set<V> visited = new HashSet<>();

        for (V start : graph.vertices()) {
            if (!visited.add(start)) {
                continue;
            }
            Deque<V> nodeStack = new ArrayDeque<>();
            Deque<Iterator<V>> iteratorStack = new ArrayDeque<>();
            nodeStack.push(start);
            iteratorStack.push(graph.successors(start).iterator());

            while (!nodeStack.isEmpty()) {
                Iterator<V> successors = iteratorStack.peek();
                V descendant = nextUnvisited(successors, visited);
                if (descendant != null) {
                    nodeStack.push(descendant);
                    iteratorStack.push(graph.successors(descendant).iterator());
                } else {
                    // No more children: this vertex is finished.
                    finishOrder.add(nodeStack.pop());
                    iteratorStack.pop();
                }
            }
        }
        return finishOrder;
    }

    /**
     * Second pass: process vertices in reverse finish order over the transpose.
     * Each iterative DFS that starts from an as-yet-unassigned vertex collects
     * exactly one strongly connected component.
     */
    private StronglyConnectedComponentsResult<V> collectComponents(
            DirectedGraph<V> transpose, List<V> finishOrder) {

        Set<V> assigned = new HashSet<>();
        List<StronglyConnectedComponent<V>> components = new ArrayList<>();

        for (int i = finishOrder.size() - 1; i >= 0; i--) {
            V root = finishOrder.get(i);
            if (!assigned.add(root)) {
                continue;
            }
            List<V> members = new ArrayList<>();
            Deque<V> stack = new ArrayDeque<>();
            stack.push(root);

            while (!stack.isEmpty()) {
                V vertex = stack.pop();
                members.add(vertex);
                for (V neighbour : transpose.successors(vertex)) {
                    if (assigned.add(neighbour)) {
                        stack.push(neighbour);
                    }
                }
            }
            components.add(new StronglyConnectedComponent<>(members));
        }
        return new StronglyConnectedComponentsResult<>(components);
    }

    /**
     * Advances the iterator past already-visited vertices, marking and
     * returning the first unvisited one (or {@code null} if none remain).
     */
    private V nextUnvisited(Iterator<V> successors, Set<V> visited) {
        while (successors.hasNext()) {
            V candidate = successors.next();
            if (visited.add(candidate)) {
                return candidate;
            }
        }
        return null;
    }
}
