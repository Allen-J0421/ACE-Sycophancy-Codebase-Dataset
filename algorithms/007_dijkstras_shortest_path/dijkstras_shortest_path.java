import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

class Dijkstra {

    static ArrayList<Integer> dijkstra(ArrayList<ArrayList<int[]>> adj, int src) {
        int V = adj.size();

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);

        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);

        dist[src] = 0;
        pq.offer(new int[]{0, src});

        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int d = top[0];
            int u = top[1];

            if (d > dist[u])
                continue;

            for (int[] p : adj.get(u)) {
                int v = p[0];
                int w = p[1];

                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pq.offer(new int[]{dist[v], v});
                }
            }
        }

        ArrayList<Integer> result = new ArrayList<>();
        for (int d : dist)
            result.add(d);

        return result;
    }

    static void addEdge(ArrayList<ArrayList<int[]>> adj, int u, int v, int w) {
        adj.get(u).add(new int[]{v, w});
        adj.get(v).add(new int[]{u, w});
    }

     public static void main(String[] args) {
        int V = 5;
        int src = 0;

        ArrayList<ArrayList<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }

        addEdge(adj, 0, 1, 4);
        addEdge(adj, 0, 2, 8);
        addEdge(adj, 1, 4, 6);
        addEdge(adj, 1, 2, 3);
        addEdge(adj, 2, 3, 2);
        addEdge(adj, 3, 4, 10);

        ArrayList<Integer> result = dijkstra(adj, src);
        for (int d : result)
            System.out.print(d + " ");
        System.out.println();
    }
}
