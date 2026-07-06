final class IntVertexIndex implements VertexIndex {

    private final int count;

    IntVertexIndex(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Vertex count must be positive, got: " + count);
        }
        this.count = count;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public void validate(int index, String role) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException(
                "Vertex " + role + "=" + index + " is out of range [0," + (count - 1) + "]");
        }
    }
}
