package graph.traversal;

import java.util.List;
import java.util.StringJoiner;

public final class TraversalResult {
    private final List<Integer> visitOrder;

    private TraversalResult(List<Integer> visitOrder) {
        this.visitOrder = List.copyOf(visitOrder);
    }

    static TraversalResult fromVisitOrder(List<Integer> visitOrder) {
        return new TraversalResult(visitOrder);
    }

    public List<Integer> visitOrder() {
        return visitOrder;
    }

    public boolean isEmpty() {
        return visitOrder.isEmpty();
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ");
        for (int vertex : visitOrder) {
            joiner.add(String.valueOf(vertex));
        }
        return joiner.toString();
    }
}
