package graph.algorithm;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DepthFirstSearch extends AbstractGraphTraversal {

    private enum TraversalMode {
        RECURSIVE("DFS (Recursive)"), ITERATIVE("DFS (Iterative)");

        private final String displayName;

        TraversalMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final TraversalMode mode;

    private DepthFirstSearch(TraversalMode mode) {
        this.mode = mode;
    }

    public static DepthFirstSearch recursive() {
        return new DepthFirstSearch(TraversalMode.RECURSIVE);
    }

    public static DepthFirstSearch iterative() {
        return new DepthFirstSearch(TraversalMode.ITERATIVE);
    }

    @Override
    public String getAlgorithmName() {
        return mode.getDisplayName();
    }

    @Override
    protected void traverseFromVertex(int startVertex) {
        if (mode == TraversalMode.RECURSIVE) {
            dfsRecursive(startVertex);
        } else {
            dfsIterative(startVertex);
        }
    }

    private void dfsRecursive(int vertex) {
        markVisited(vertex);
        addToResult(vertex);

        for (int neighbor : graph.getNeighbors(vertex)) {
            if (!isVisited(neighbor)) {
                dfsRecursive(neighbor);
            }
        }
    }

    private void dfsIterative(int start) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            int vertex = stack.pop();

            if (!isVisited(vertex)) {
                markVisited(vertex);
                addToResult(vertex);

                List<Integer> neighbors = new ArrayList<>(graph.getNeighbors(vertex));
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    int neighbor = neighbors.get(i);
                    if (!isVisited(neighbor)) {
                        stack.push(neighbor);
                    }
                }
            }
        }
    }
}
