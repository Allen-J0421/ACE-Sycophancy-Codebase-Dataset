import java.util.List;
import java.util.Set;

public interface IGraph {
    void addEdge(int u, int v);

    void removeEdge(int u, int v);

    List<Integer> getNeighbors(int vertex);

    int getVertexCount();

    int getEdgeCount();

    boolean hasEdge(int u, int v);

    int getDegree(int vertex);

    boolean isEmpty();

    List<Integer> getAllVertices();

    Set<Integer> getVerticesWithDegree(int degree);
}
