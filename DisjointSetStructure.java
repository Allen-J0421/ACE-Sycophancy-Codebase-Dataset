public class DisjointSetStructure {
    private int[] parent;
    private int[] rank;
    private UnionStrategy strategy;

    public DisjointSetStructure(int size, UnionStrategy strategy) {
        this.parent = new int[size];
        this.rank = new int[size];
        this.strategy = strategy;
        initialize();
    }

    public DisjointSetStructure(int size) {
        this(size, new RankBasedUnionStrategy());
    }

    public int find(int i) {
        if (parent[i] != i) {
            parent[i] = find(parent[i]);
        }
        return parent[i];
    }

    public void union(int irep, int jrep) {
        if (irep == jrep) {
            return;
        }
        strategy.union(parent, rank, irep, jrep);
    }

    public void reset() {
        strategy.reset(parent, rank);
    }

    public void setStrategy(UnionStrategy newStrategy) {
        this.strategy = newStrategy;
    }

    private void initialize() {
        strategy.reset(parent, rank);
    }
}
