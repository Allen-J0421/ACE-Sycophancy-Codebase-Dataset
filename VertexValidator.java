class VertexValidator implements Validator<Integer> {
    private final int vertexCount;

    VertexValidator(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    @Override
    public void validate(Integer vertex) throws IllegalArgumentException {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                String.format("Vertex %d is out of range [0, %d)", vertex, vertexCount)
            );
        }
    }
}
