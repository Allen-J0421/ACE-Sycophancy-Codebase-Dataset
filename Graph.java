import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

class Graph implements WeightedGraphView {
    private final List<List<Edge>> adjacencyList;
    private final int vertexCount;
    private final VertexValidator vertexValidator;
    private static final BiPredicate<Integer, Integer> NO_SELF_LOOPS = (src, dst) -> src != dst;

    private Graph(int vertexCount) {
        if (vertexCount <= 0) {
            throw new IllegalArgumentException("Vertex count must be positive");
        }
        this.vertexCount = vertexCount;
        this.vertexValidator = new VertexValidator(vertexCount);
        this.adjacencyList = new ArrayList<>();
        IntStream.range(0, vertexCount)
            .forEach(i -> adjacencyList.add(new ArrayList<>()));
    }

    static Graph create(int vertexCount) {
        return new Graph(vertexCount);
    }

    void addEdge(int source, int destination, int weight) {
        vertexValidator.validate(source);
        vertexValidator.validate(destination);
        if (!NO_SELF_LOOPS.test(source, destination)) {
            throw new IllegalArgumentException("Self-loops are not supported");
        }
        adjacencyList.get(source).add(Edge.of(destination, weight));
        adjacencyList.get(destination).add(Edge.of(source, weight));
    }

    @Override
    public List<Edge> getAdjacencyListFor(int vertex) {
        vertexValidator.validate(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

    @Override
    public String toString() {
        return String.format("Graph(vertices=%d, edges=%d)", vertexCount, countEdges());
    }

    private int countEdges() {
        return adjacencyList.stream().mapToInt(List::size).sum() / 2;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Graph)) return false;
        Graph other = (Graph) obj;
        return vertexCount == other.vertexCount && adjacencyList.equals(other.adjacencyList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexCount, adjacencyList);
    }
}
