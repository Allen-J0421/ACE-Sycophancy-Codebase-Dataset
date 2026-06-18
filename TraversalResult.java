import java.util.List;
import java.util.StringJoiner;

final class TraversalResult {
    private final List<Integer> visitOrder;

    private TraversalResult(List<Integer> visitOrder) {
        this.visitOrder = List.copyOf(visitOrder);
    }

    static TraversalResult fromVisitOrder(List<Integer> visitOrder) {
        return new TraversalResult(visitOrder);
    }

    List<Integer> visitOrder() {
        return visitOrder;
    }

    boolean isEmpty() {
        return visitOrder.isEmpty();
    }

    String formatVisitOrder() {
        StringJoiner joiner = new StringJoiner(" ");
        for (int vertex : visitOrder) {
            joiner.add(String.valueOf(vertex));
        }
        return joiner.toString();
    }
}
