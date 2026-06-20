import java.util.List;

public final class StronglyConnectedComponents {

    private StronglyConnectedComponents() {
    }

    public static void main(String[] args) {
        DirectedGraph graph = DirectedGraph.fromEdges(
            List.of(Vertex.of(0), Vertex.of(1), Vertex.of(2), Vertex.of(3), Vertex.of(4)),
            Edge.of(0, 2),
            Edge.of(0, 3),
            Edge.of(1, 0),
            Edge.of(2, 1),
            Edge.of(3, 4)
        );

        StronglyConnectedComponentsFinder solver = new KosarajuStronglyConnectedComponentsFinder();
        StronglyConnectedComponentsResult result = solver.findComponents(graph);
        StronglyConnectedComponentsFormatter formatter = new StronglyConnectedComponentsFormatter();

        System.out.println(formatter.format(result));
    }
}
