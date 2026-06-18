import java.util.List;

interface Graph {
    int vertexCount();

    List<Integer> neighborsOf(int vertex);

    default boolean isEmpty() {
        return vertexCount() == 0;
    }
}
