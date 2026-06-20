import java.util.List;

interface Graph {

    int vertexCount();

    List<Vertex> vertices();

    List<Vertex> neighborsOf(Vertex vertex);
}
