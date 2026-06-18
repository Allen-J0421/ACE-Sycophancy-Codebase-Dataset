package graph.visitor;

import java.util.ArrayList;
import java.util.List;

public class CollectingVisitor implements VertexVisitor {
    private final List<Integer> visited;

    public CollectingVisitor() {
        this.visited = new ArrayList<>();
    }

    @Override
    public void visit(int vertex) {
        visited.add(vertex);
    }

    public List<Integer> getVisited() {
        return new ArrayList<>(visited);
    }

    public void clear() {
        visited.clear();
    }
}
