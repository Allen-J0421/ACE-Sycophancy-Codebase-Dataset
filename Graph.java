import java.util.List;

public interface Graph {
    void addEdge(int u, int v);
    List<Integer> getAdjacent(int vertex);
    int getVertexCount();
    boolean isValidVertex(int vertex);
}
