import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

interface Graph {
    int size();
    Iterable<Integer> getNeighbors(int v);
}

class AdjacencyListGraph implements Graph {
    private ArrayList<ArrayList<Integer>> adj;
    private int V;

    AdjacencyListGraph(int V) {
        this.V = V;
        adj = new ArrayList<>();
        for (int i = 0; i < V; i++)
            adj.add(new ArrayList<>());
    }

    void addEdge(int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    @Override
    public int size() {
        return V;
    }

    @Override
    public Iterable<Integer> getNeighbors(int v) {
        return adj.get(v);
    }
}

class GraphTraversal {

    static class BFSContext {
        private boolean[] visited;
        private ArrayList<Integer> component;

        BFSContext(int V) {
            visited = new boolean[V];
            component = new ArrayList<>();
        }

        boolean isVisited(int v) {
            return visited[v];
        }

        void markVisited(int v) {
            visited[v] = true;
        }

        void addToComponent(int v) {
            component.add(v);
        }

        void resetComponent() {
            component = new ArrayList<>();
        }

        ArrayList<Integer> getComponent() {
            return component;
        }
    }

    static void bfs(Graph g, int src, BFSContext ctx) {
        Queue<Integer> q = new LinkedList<>();
        ctx.markVisited(src);
        q.add(src);

        while (!q.isEmpty()) {
            int curr = q.poll();
            ctx.addToComponent(curr);

            for (int x : g.getNeighbors(curr)) {
                if (!ctx.isVisited(x)) {
                    ctx.markVisited(x);
                    q.add(x);
                }
            }
        }
    }
}

class ConnectedComponents {

    static ArrayList<ArrayList<Integer>> getComponents(Graph g) {
        GraphTraversal.BFSContext ctx = new GraphTraversal.BFSContext(g.size());
        ArrayList<ArrayList<Integer>> res = new ArrayList<>();

        for (int i = 0; i < g.size(); i++) {
            if (!ctx.isVisited(i)) {
                ctx.resetComponent();
                GraphTraversal.bfs(g, i, ctx);
                res.add(ctx.getComponent());
            }
        }
        return res;
    }

    public static void main(String[] args) {
        AdjacencyListGraph g = new AdjacencyListGraph(6);

        g.addEdge(1, 2);
        g.addEdge(0, 3);
        g.addEdge(2, 0);
        g.addEdge(5, 4);

        ArrayList<ArrayList<Integer>> res = getComponents(g);

        for (ArrayList<Integer> component : res) {
            for (int vertex : component) {
                System.out.print(vertex + " ");
            }
            System.out.println();
        }
    }
}
