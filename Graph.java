import java.util.List;

public interface Graph {
    int vertexCount();

    List<Vertex> vertices();

    List<Vertex> neighborsOf(Vertex vertex);

    boolean containsVertex(Vertex vertex);

    Graph reverse();
}
