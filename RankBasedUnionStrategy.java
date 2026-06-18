public class RankBasedUnionStrategy implements UnionStrategy {

    @Override
    public void union(int[] parent, int[] rank, int irep, int jrep) {
        if (rank[irep] < rank[jrep]) {
            parent[irep] = jrep;
        } else if (rank[irep] > rank[jrep]) {
            parent[jrep] = irep;
        } else {
            parent[jrep] = irep;
            rank[irep]++;
        }
    }

    @Override
    public void reset(int[] parent, int[] rank) {
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }
}
