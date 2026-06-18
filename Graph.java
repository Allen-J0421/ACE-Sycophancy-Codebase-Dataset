import java.util.List;

interface Graph {
    int vertexCount();

    List<Integer> neighborsOf(int vertex);
}
