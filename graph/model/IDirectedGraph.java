package graph.model;

import java.util.List;

public interface IDirectedGraph {
    void addDirectedEdge(int from, int to, double weight);

    double getEdgeWeight(int from, int to);

    List<Integer> getOutgoing(int vertex);

    List<Integer> getIncoming(int vertex);

    int getVertexCount();

    int getEdgeCount();

    boolean hasEdge(int from, int to);

    int getInDegree(int vertex);

    int getOutDegree(int vertex);

    IDirectedGraph reverse();
}
