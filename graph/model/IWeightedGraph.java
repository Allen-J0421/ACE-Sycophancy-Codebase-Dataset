package graph.model;

import java.util.List;

public interface IWeightedGraph {
    void addWeightedEdge(int u, int v, double weight);

    double getEdgeWeight(int u, int v);

    List<Edge> getEdges(int vertex);

    List<Edge> getAllEdges();

    int getVertexCount();

    int getEdgeCount();

    boolean hasEdge(int u, int v);
}
