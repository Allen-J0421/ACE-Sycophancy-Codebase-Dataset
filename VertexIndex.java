interface VertexIndex {

    int size();

    // Throws IllegalArgumentException if index is not a valid vertex for this graph.
    void validate(int index, String role);
}
