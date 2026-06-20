final class Vertex {

    private final int index;

    Vertex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Vertex index must be non-negative: " + index);
        }

        this.index = index;
    }

    int index() {
        return index;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vertex)) {
            return false;
        }

        Vertex vertex = (Vertex) other;
        return index == vertex.index;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(index);
    }

    @Override
    public String toString() {
        return Integer.toString(index);
    }
}
