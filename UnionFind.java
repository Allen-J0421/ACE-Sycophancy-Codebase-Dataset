public class UnionFind implements DisjointSet {
    private DisjointSetStructure structure;
    private IndexValidator validator;
    private OperationCounter counter;

    private UnionFind(int size, UnionStrategy unionStrategy, FindStrategy findStrategy) {
        this.validator = new IndexValidator(size);
        this.structure = new DisjointSetStructure(size, unionStrategy, findStrategy);
        this.counter = new OperationCounter();
    }

    private UnionFind(int size, UnionStrategy strategy) {
        this(size, strategy, new PathCompressionFindStrategy());
    }

    private UnionFind(int size) {
        this(size, new RankBasedUnionStrategy());
    }

    @Override
    public int find(int i) {
        validator.validate(i);
        counter.incrementFind();
        return structure.find(i);
    }

    @Override
    public void union(int i, int j) {
        validator.validate(i);
        validator.validate(j);
        counter.incrementUnion();

        int irep = structure.find(i);
        int jrep = structure.find(j);
        structure.union(irep, jrep);
    }

    @Override
    public boolean isConnected(int i, int j) {
        validator.validate(i);
        validator.validate(j);
        return find(i) == find(j);
    }

    @Override
    public int getSize() {
        return validator.getSize();
    }

    @Override
    public Statistics getStatistics() {
        return counter.toStatistics();
    }

    public void reset() {
        structure.reset();
        counter.reset();
    }

    public static UnionFindBuilder builder() {
        return new UnionFindBuilder();
    }

    public static class UnionFindBuilder {
        private int size;
        private UnionStrategy unionStrategy = new RankBasedUnionStrategy();
        private FindStrategy findStrategy = new PathCompressionFindStrategy();

        public UnionFindBuilder withSize(int size) {
            this.size = size;
            return this;
        }

        public UnionFindBuilder withUnionStrategy(UnionStrategy strategy) {
            this.unionStrategy = strategy;
            return this;
        }

        public UnionFindBuilder withStrategy(UnionStrategy strategy) {
            return withUnionStrategy(strategy);
        }

        public UnionFindBuilder withFindStrategy(FindStrategy strategy) {
            this.findStrategy = strategy;
            return this;
        }

        public UnionFind build() {
            if (size <= 0) {
                throw new IllegalArgumentException("Size must be set and positive");
            }
            return new UnionFind(size, unionStrategy, findStrategy);
        }
    }
}
