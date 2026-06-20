import java.util.Arrays;

class KruskalMST {

    static class Edge implements Comparable<Edge> {
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
    }

    public static int kruskalsMST(int vertexCount, Edge[] edges) {
        Edge[] sorted = edges.clone();
        Arrays.sort(sorted);

        UnionFind uf = new UnionFind(vertexCount);
        int totalCost = 0;
        int edgesAdded = 0;

        for (Edge edge : sorted) {
            if (uf.union(edge.from, edge.to)) {
                totalCost += edge.weight;
                if (++edgesAdded == vertexCount - 1) break;
            }
        }
        return totalCost;
    }

    public static void main(String[] args) {
        Edge[] edges = {
            new Edge(0, 1, 10),
            new Edge(1, 3, 15),
            new Edge(2, 3, 4),
            new Edge(2, 0, 6),
            new Edge(0, 3, 5)
        };

        System.out.println(kruskalsMST(4, edges));
    }

    private static class UnionFind {
        private final int[] parent, rank;

        UnionFind(int n) {
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

        boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX == rootY) return false;

            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
            return true;
        }
    }
}
