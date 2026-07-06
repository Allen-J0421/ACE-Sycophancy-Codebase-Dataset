import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;

class Graph {
    private final int V;
    private final ArrayList<ArrayList<Integer>> adj;

    private Graph(int V, ArrayList<ArrayList<Integer>> adj) {
        this.V = V;
        this.adj = adj;
    }

    static Graph of(int V, Iterable<int[]> edges) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }
        return new Graph(V, adj);
    }

    int getV() { return V; }
    ArrayList<ArrayList<Integer>> getAdj() { return adj; }
}

class ArticulationResult implements Iterable<Integer> {
    private final ArrayList<Integer> points;

    ArticulationResult(ArrayList<Integer> points) {
        this.points = points;
    }

    boolean hasArticulationPoints() { return !points.isEmpty(); }
    ArrayList<Integer> getPoints() { return points; }

    @Override
    public Iterator<Integer> iterator() { return points.iterator(); }
}

interface GraphAlgorithm<T> {
    T execute();
}

class ArticulationPointsFinder implements GraphAlgorithm<ArticulationResult> {
    private final Graph graph;

    ArticulationPointsFinder(Graph graph) {
        this.graph = graph;
    }

    private static class TraversalState {
        int[] disc, low, visited, isAP;
        int time;

        TraversalState(int V) {
            disc = new int[V];
            low = new int[V];
            visited = new int[V];
            isAP = new int[V];
            time = 0;
        }
    }

    private static class Frame {
        final int u, parent;
        int children, lastChild;
        final Iterator<Integer> iter;

        Frame(int u, int parent, Iterator<Integer> iter) {
            this.u = u;
            this.parent = parent;
            this.children = 0;
            this.lastChild = -1;
            this.iter = iter;
        }

        // Returns the child that just returned, or -1 if none pending.
        int consumeLastChild() {
            int v = lastChild;
            lastChild = -1;
            return v;
        }
    }

    private static class DfsStack {
        private final Deque<Frame> deque = new ArrayDeque<>();

        boolean isEmpty() { return deque.isEmpty(); }
        Frame current() { return deque.peek(); }

        void push(int u, int parent, Iterator<Integer> iter) {
            deque.push(new Frame(u, parent, iter));
        }

        // Pops the finished frame and notifies the new top that a child returned.
        void pop() {
            int u = deque.pop().u;
            if (!deque.isEmpty()) deque.peek().lastChild = u;
        }
    }

    private void dfs(int start, int startParent, TraversalState state) {
        ArrayList<ArrayList<Integer>> adj = graph.getAdj();
        DfsStack stack = new DfsStack();

        state.visited[start] = 1;
        state.disc[start] = state.low[start] = ++state.time;
        stack.push(start, startParent, adj.get(start).iterator());

        while (!stack.isEmpty()) {
            Frame frame = stack.current();
            int u = frame.u;

            int returned = frame.consumeLastChild();
            if (returned != -1) {
                state.low[u] = Math.min(state.low[u], state.low[returned]);
                if (frame.parent != -1 && state.low[returned] >= state.disc[u]) {
                    state.isAP[u] = 1;
                }
            }

            if (frame.iter.hasNext()) {
                int v = frame.iter.next();
                if (state.visited[v] == 0) {
                    frame.children++;
                    state.visited[v] = 1;
                    state.disc[v] = state.low[v] = ++state.time;
                    stack.push(v, u, adj.get(v).iterator());
                } else if (v != frame.parent) {
                    state.low[u] = Math.min(state.low[u], state.disc[v]);
                }
            } else {
                if (frame.parent == -1 && frame.children > 1) {
                    state.isAP[u] = 1;
                }
                stack.pop();
            }
        }
    }

    public ArticulationResult execute() {
        int V = graph.getV();
        TraversalState state = new TraversalState(V);

        for (int u = 0; u < V; u++) {
            if (state.visited[u] == 0) {
                dfs(u, -1, state);
            }
        }

        ArrayList<Integer> points = new ArrayList<>();
        for (int u = 0; u < V; u++) {
            if (state.isAP[u] == 1) points.add(u);
        }

        return new ArticulationResult(points);
    }
}

class ArticulationPoints {
    public static void main(String[] args) {
        int V = 5;
        Iterable<int[]> edges = Arrays.asList(
            new int[]{0, 1}, new int[]{1, 4}, new int[]{2, 3},
            new int[]{2, 4}, new int[]{3, 4}
        );

        Graph g = Graph.of(V, edges);
        GraphAlgorithm<ArticulationResult> finder = new ArticulationPointsFinder(g);
        ArticulationResult result = finder.execute();

        for (int u : result) {
            System.out.print(u + " ");
        }
        System.out.println();
    }
}
