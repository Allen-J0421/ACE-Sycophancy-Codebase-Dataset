public class UnionFind implements DisjointSet {
    private DisjointSetStructure structure;
    private IndexValidator validator;
    private OperationCounter counter;

    private UnionFind(int size) {
        this.validator = new IndexValidator(size);
        this.structure = new DisjointSetStructure(size);
        this.counter = new OperationCounter();
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

        public UnionFindBuilder withSize(int size) {
            this.size = size;
            return this;
        }

        public UnionFind build() {
            if (size <= 0) {
                throw new IllegalArgumentException("Size must be set and positive");
            }
            return new UnionFind(size);
        }
    }
}
