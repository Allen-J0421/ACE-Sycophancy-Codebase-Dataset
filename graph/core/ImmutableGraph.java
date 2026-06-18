package graph.core;

import java.util.List;
import java.util.Set;

public class ImmutableGraph implements IGraph {
    private final Graph delegate;

    public ImmutableGraph(Graph graph) {
        this.delegate = (Graph) graph.copy();
    }

    @Override
    public void addEdge(int u, int v) {
        throw new UnsupportedOperationException("Cannot modify immutable graph");
    }

    @Override
    public void removeEdge(int u, int v) {
        throw new UnsupportedOperationException("Cannot modify immutable graph");
    }

    @Override
    public List<Integer> getNeighbors(int vertex) {
        return delegate.getNeighbors(vertex);
    }

    @Override
    public int getVertexCount() {
        return delegate.getVertexCount();
    }

    @Override
    public int getEdgeCount() {
        return delegate.getEdgeCount();
    }

    @Override
    public boolean hasEdge(int u, int v) {
        return delegate.hasEdge(u, v);
    }

    @Override
    public int getDegree(int vertex) {
        return delegate.getDegree(vertex);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public List<Integer> getAllVertices() {
        return delegate.getAllVertices();
    }

    @Override
    public Set<Integer> getVerticesWithDegree(int degree) {
        return delegate.getVerticesWithDegree(degree);
    }

    @Override
    public IGraph copy() {
        return new ImmutableGraph(delegate);
    }
}
