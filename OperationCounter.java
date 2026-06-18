public class OperationCounter {
    private long finds;
    private long unions;

    public OperationCounter() {
        this.finds = 0;
        this.unions = 0;
    }

    public void incrementFind() {
        finds++;
    }

    public void incrementUnion() {
        unions++;
    }

    public long getFinds() {
        return finds;
    }

    public long getUnions() {
        return unions;
    }

    public void reset() {
        finds = 0;
        unions = 0;
    }

    public DisjointSet.Statistics toStatistics() {
        return new DisjointSet.Statistics(finds, unions);
    }
}
