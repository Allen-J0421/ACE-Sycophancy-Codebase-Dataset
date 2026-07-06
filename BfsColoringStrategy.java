import java.util.LinkedList;
import java.util.Queue;

class BfsColoringStrategy implements ColoringStrategy {

    @Override
    public boolean colorComponent(Graph graph, int start, BipartiteChecker.Coloring coloring) {
        Queue<Integer> queue = new LinkedList<>();
        coloring.set(start, BipartiteChecker.Color.RED);
        queue.offer(start);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : graph.neighbors(u)) {
                if (coloring.isUncolored(v)) {
                    coloring.set(v, coloring.get(u).opposite());
                    queue.offer(v);
                } else if (coloring.get(v) == coloring.get(u)) {
                    return false;
                }
            }
        }
        return true;
    }
}
