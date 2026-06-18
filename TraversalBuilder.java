import java.util.ArrayList;
import java.util.List;

public class TraversalBuilder {
    private final Graph graph;
    private GraphTraversal strategy;
    private List<VertexVisitor> visitors;

    public TraversalBuilder(Graph graph) {
        this.graph = graph;
        this.visitors = new ArrayList<>();
    }

    public TraversalBuilder withStrategy(GraphTraversal strategy) {
        this.strategy = strategy;
        return this;
    }

    public TraversalBuilder addVisitor(VertexVisitor visitor) {
        this.visitors.add(visitor);
        return this;
    }

    public TraversalResult execute() {
        if (strategy == null) {
            strategy = new BreadthFirstSearch();
        }
        TraversalResult result = strategy.traverse(graph);
        notifyVisitors(result);
        return result;
    }

    private void notifyVisitors(TraversalResult result) {
        for (int vertex : result.getVertices()) {
            for (VertexVisitor visitor : visitors) {
                visitor.visit(vertex);
            }
        }
    }
}
