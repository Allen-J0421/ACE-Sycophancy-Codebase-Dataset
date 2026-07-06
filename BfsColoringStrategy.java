import java.util.LinkedList;
import java.util.Queue;

class BfsColoringStrategy implements ColoringStrategy {

    @Override
    public boolean colorComponent(Graph graph, int start, PartitionState state) {
        Queue<Integer> queue = new LinkedList<>();
        state.setColor(start, PartitionState.Color.RED);
        queue.offer(start);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : graph.neighbors(u)) {
                if (state.isUncolored(v)) {
                    state.setColor(v, state.getColor(u).opposite());
                    queue.offer(v);
                } else if (state.getColor(v) == state.getColor(u)) {
                    return false;
                }
            }
        }
        return true;
    }
}
