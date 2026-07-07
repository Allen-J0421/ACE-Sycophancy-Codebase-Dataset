package graph.cycle;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class DfsCycleDetector implements CycleDetector {

    private enum Color { WHITE, GRAY, BLACK }

    private static class Frame {
        final int node;
        final Iterator<Integer> neighbors;

        Frame(int node, Iterator<Integer> neighbors) {
            this.node = node;
            this.neighbors = neighbors;
        }
    }

    @Override
    public boolean hasCycle(DirectedGraph graph) {
        int n = graph.size();
        Color[] color = new Color[n];
        for (int i = 0; i < n; i++) {
            color[i] = Color.WHITE;
        }

        Deque<Frame> stack = new ArrayDeque<>();

        for (int start = 0; start < n; start++) {
            if (color[start] != Color.WHITE) {
                continue;
            }
            color[start] = Color.GRAY;
            stack.push(new Frame(start, graph.neighbors(start).iterator()));

            while (!stack.isEmpty()) {
                Frame frame = stack.peek();
                if (frame.neighbors.hasNext()) {
                    int neighbor = frame.neighbors.next();
                    if (color[neighbor] == Color.GRAY) {
                        return true;
                    }
                    if (color[neighbor] == Color.WHITE) {
                        color[neighbor] = Color.GRAY;
                        stack.push(new Frame(neighbor, graph.neighbors(neighbor).iterator()));
                    }
                } else {
                    color[frame.node] = Color.BLACK;
                    stack.pop();
                }
            }
        }

        return false;
    }
}
