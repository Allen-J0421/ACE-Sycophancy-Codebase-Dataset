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
            Arrays.setAll(parent, i -> i);
        }

        int find(int i) {
            while (parent[i] != i) {
                parent[i] = parent[parent[i]]; // path halving
                i = parent[i];
            }
            return i;
        }

        boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX == rootY) return false;

            if (rank[rootX] > rank[rootY]) {
                int tmp = rootX; rootX = rootY; rootY = tmp;
            }
            parent[rootX] = rootY;
            if (rank[rootX] == rank[rootY]) rank[rootY]++;
            return true;
        }
    }
}
