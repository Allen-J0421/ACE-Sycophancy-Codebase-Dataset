import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;

public class DetectCycle {

    static boolean isCyclic(ArrayList<ArrayList<Integer>> adj)
    {
        int V = adj.size();

        int[] inDegree = new int[V];

        Queue<Integer> q = new LinkedList<>();

        int visited = 0;

        for (int u = 0; u < V; ++u)
        {
            for (int v : adj.get(u))
            {
                inDegree[v]++;
            }
        }

        for (int u = 0; u < V; ++u)
        {
            if (inDegree[u] == 0)
            {
                q.add(u);
            }
        }

        while (!q.isEmpty())
        {
            int u = q.poll();
            visited++;

            for (int v : adj.get(u))
            {
                inDegree[v]--;
                if (inDegree[v] == 0)
                {

                    q.add(v);
                }
            }
        }

        return visited != V;
    }

    static void addEdge(ArrayList<ArrayList<Integer>> adj, int u, int v) {
        adj.get(u).add(v);
     }

     public static void main(String[] args)
    {
        int V = 4;
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }

        addEdge(adj, 0, 1);
        addEdge(adj, 1, 2);
        addEdge(adj, 2, 0);
        addEdge(adj, 2, 3);

         System.out.println(isCyclic(adj) ? "true" : "false");
    }
}
