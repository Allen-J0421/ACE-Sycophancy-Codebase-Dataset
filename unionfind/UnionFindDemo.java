package unionfind;

import java.util.Arrays;

public class UnionFindDemo {

    static class Edge implements Comparable<Edge> {
        final int u, v, weight;
        Edge(int u, int v, int weight) { this.u = u; this.v = v; this.weight = weight; }
        @Override public int compareTo(Edge other) { return Integer.compare(this.weight, other.weight); }
        @Override public String toString() { return u + "-" + v + " (weight " + weight + ")"; }
    }

    public static void main(String[] args) {
        // Kruskal's MST: union() returning false means adding the edge would form a cycle
        Edge[] edges = {
            new Edge(0, 1, 2),
            new Edge(1, 2, 3),
            new Edge(1, 4, 5),
            new Edge(0, 3, 6),
            new Edge(2, 4, 7),
            new Edge(1, 3, 8),
            new Edge(3, 4, 9),
        };

        int vertices = 5;
        Arrays.sort(edges);
        UnionFind uf = new UnionFind(vertices);
        int mstWeight = 0;

        System.out.println("Kruskal's MST (" + vertices + " vertices, " + edges.length + " edges)");
        System.out.println();

        for (Edge e : edges) {
            if (uf.union(e.u, e.v)) {
                System.out.println("  add  " + e);
                mstWeight += e.weight;
            } else {
                System.out.println("  skip " + e + "  <- cycle");
            }
        }

        System.out.println();
        System.out.println("MST weight:     " + mstWeight);
        System.out.println("MST complete:   " + (uf.componentCount() == 1));
    }
}
