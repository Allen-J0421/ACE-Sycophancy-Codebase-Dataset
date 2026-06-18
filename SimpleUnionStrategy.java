public class SimpleUnionStrategy implements UnionStrategy {

    @Override
    public void union(int[] parent, int[] rank, int irep, int jrep) {
        parent[irep] = jrep;
    }

    @Override
    public void reset(int[] parent, int[] rank) {
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }
}
