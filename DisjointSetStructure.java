public class DisjointSetStructure {
    private int[] parent;
    private int[] rank;
    private UnionStrategy unionStrategy;
    private FindStrategy findStrategy;

    public DisjointSetStructure(int size, UnionStrategy unionStrategy, FindStrategy findStrategy) {
        this.parent = new int[size];
        this.rank = new int[size];
        this.unionStrategy = unionStrategy;
        this.findStrategy = findStrategy;
        initialize();
    }

    public DisjointSetStructure(int size, UnionStrategy unionStrategy) {
        this(size, unionStrategy, new PathCompressionFindStrategy());
    }

    public DisjointSetStructure(int size) {
        this(size, new RankBasedUnionStrategy(), new PathCompressionFindStrategy());
    }

    public int find(int i) {
        return findStrategy.find(parent, i);
    }

    public void union(int irep, int jrep) {
        if (irep == jrep) {
            return;
        }
        unionStrategy.union(parent, rank, irep, jrep);
    }

    public void reset() {
        unionStrategy.reset(parent, rank);
        findStrategy.reset(parent);
    }

    public void setUnionStrategy(UnionStrategy newStrategy) {
        this.unionStrategy = newStrategy;
    }

    public void setFindStrategy(FindStrategy newStrategy) {
        this.findStrategy = newStrategy;
    }

    private void initialize() {
        unionStrategy.reset(parent, rank);
        findStrategy.reset(parent);
    }
}
