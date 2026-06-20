package com.algorithms.scc;

/**
 * Strategy for decomposing a directed graph into its strongly connected
 * components. Different algorithms (Kosaraju, Tarjan, ...) can be plugged in
 * behind this interface without affecting callers.
 *
 * @param <V> the vertex label type
 */
public interface StronglyConnectedComponentsFinder<V> {

    /**
     * Finds every strongly connected component of the given graph.
     *
     * @param graph the directed graph to analyse
     * @return the discovered components
     */
    StronglyConnectedComponentsResult<V> find(DirectedGraph<V> graph);
}
