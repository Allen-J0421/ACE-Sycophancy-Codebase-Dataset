import java.util.List;

interface Graph {
    int vertexCount();

    List<Neighbor> neighborsOf(int vertex);
}
