import java.util.List;

public interface Graph {
    int vertexCount();

    List<Integer> vertices();

    List<Integer> neighborsOf(int vertex);

    boolean containsVertex(int vertex);

    Graph reverse();
}
