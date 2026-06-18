public interface UnionStrategy {
    void union(int[] parent, int[] rank, int irep, int jrep);
    void reset(int[] parent, int[] rank);
}
