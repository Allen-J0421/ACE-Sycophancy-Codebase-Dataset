public interface IntGraph {
    int vertexCount();

    int neighborCount(int vertex);

    int neighborAt(int vertex, int neighborIndex);
}
