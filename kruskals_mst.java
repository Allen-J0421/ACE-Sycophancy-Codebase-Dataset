import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class KruskalMST {
    public static List<Edge> kruskalsMST(int V, Edge[] edges) {
        Arrays.sort(edges);

        DisjointSet ds = new DisjointSet(V);
        List<Edge> mst = new ArrayList<>();

        for (Edge e : edges) {
            if (ds.find(e.from) != ds.find(e.to)) {
                ds.union(e.from, e.to);
                mst.add(e);
                if (mst.size() == V - 1) break;
            }
        }
        return mst;
    }

    public static void main(String[] args) {
        Edge[] edges = {
            new Edge(0, 1, 10),
            new Edge(1, 3, 15),
            new Edge(2, 3, 4),
            new Edge(2, 0, 6),
            new Edge(0, 3, 5)
        };

        List<Edge> mst = kruskalsMST(4, edges);
        int totalCost = mst.stream().mapToInt(e -> e.weight).sum();
        System.out.println("MST edges: " + mst);
        System.out.println("Total cost: " + totalCost);
    }
}

class Edge implements Comparable<Edge> {
    final int from, to, weight;

    Edge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    @Override
    public String toString() {
        return "(" + from + "-" + to + ", w=" + weight + ")";
    }
}

class DisjointSet {
    private final int[] parent, rank;

    DisjointSet(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 1;
        }
    }

    int find(int i) {
        if (parent[i] != i) {
            parent[i] = find(parent[i]);
        }
        return parent[i];
    }

    void union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return;
        if (rank[rx] < rank[ry]) {
            parent[rx] = ry;
        } else if (rank[rx] > rank[ry]) {
            parent[ry] = rx;
        } else {
            parent[ry] = rx;
            rank[rx]++;
        }
    }
}
