public final class NextHopTable {

    private final IntSquareMatrix nextHop;

    private NextHopTable(IntSquareMatrix nextHop) {
        this.nextHop = nextHop;
    }

    public static NextHopTable from(int[][] nextHop) {
        return new NextHopTable(IntSquareMatrix.from(nextHop));
    }

    public VertexPath path(int source, int target) {
        validateVertex(source, nextHop.size());
        validateVertex(target, nextHop.size());

        if (nextHop.get(source, target) == -1) {
            return VertexPath.empty();
        }

        java.util.ArrayList<Integer> vertices = new java.util.ArrayList<>();
        int current = source;
        vertices.add(current);

        while (current != target) {
            current = nextHop.get(current, target);
            if (current == -1) {
                return VertexPath.empty();
            }
            vertices.add(current);
        }

        return VertexPath.of(vertices);
    }

    public boolean isReachable(int source, int target) {
        validateVertex(source, nextHop.size());
        validateVertex(target, nextHop.size());
        return nextHop.get(source, target) != -1;
    }

    public int size() {
        return nextHop.size();
    }

    private static void validateVertex(int vertex, int size) {
        if (vertex < 0 || vertex >= size) {
            throw new IllegalArgumentException("Vertex index out of bounds: " + vertex);
        }
    }
}
