public interface DisjointSet {
    int find(int i);
    void union(int i, int j);
    boolean isConnected(int i, int j);
    int getSize();
    Statistics getStatistics();

    class Statistics {
        public final long findOperations;
        public final long unionOperations;

        public Statistics(long findOperations, long unionOperations) {
            this.findOperations = findOperations;
            this.unionOperations = unionOperations;
        }

        @Override
        public String toString() {
            return String.format("Stats{finds=%d, unions=%d}", findOperations, unionOperations);
        }
    }
}
