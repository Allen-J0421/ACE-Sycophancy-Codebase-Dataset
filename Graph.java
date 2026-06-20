public interface Graph {
    int vertexCount();

    Iterable<Integer> neighbors(int vertex);
}
