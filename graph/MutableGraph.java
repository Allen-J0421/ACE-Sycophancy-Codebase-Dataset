package graph;

public interface MutableGraph extends Graph {
    void addDirectedEdge(int from, int to);

    default void addUndirectedEdge(int from, int to) {
        addDirectedEdge(from, to);
        addDirectedEdge(to, from);
    }
}
