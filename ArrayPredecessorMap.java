import java.util.Arrays;

final class ArrayPredecessorMap implements PredecessorMap {

    private final int[] pred;

    ArrayPredecessorMap(int vertices) {
        pred = new int[vertices];
        Arrays.fill(pred, Distances.NO_PREDECESSOR);
    }

    @Override
    public void set(int vertex, int predecessor) {
        pred[vertex] = predecessor;
    }

    @Override
    public int predecessorOf(int vertex) {
        return pred[vertex];
    }
}
