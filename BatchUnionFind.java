import java.util.ArrayList;
import java.util.List;

public class BatchUnionFind implements UnionFindDelegate {
    private UnionFindDelegate delegate;
    private List<UnionOperation> pendingOperations;
    private boolean batchMode;

    private static class UnionOperation {
        int i;
        int j;

        UnionOperation(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    public BatchUnionFind(UnionFindDelegate delegate) {
        this.delegate = delegate;
        this.pendingOperations = new ArrayList<>();
        this.batchMode = false;
    }

    public void startBatch() {
        batchMode = true;
        pendingOperations.clear();
    }

    public void union(int i, int j) {
        if (batchMode) {
            pendingOperations.add(new UnionOperation(i, j));
        } else {
            delegate.union(i, j);
        }
    }

    public int flushBatch() {
        if (!batchMode) {
            return 0;
        }

        int count = pendingOperations.size();
        for (UnionOperation op : pendingOperations) {
            delegate.union(op.i, op.j);
        }
        pendingOperations.clear();
        batchMode = false;
        return count;
    }

    public int find(int i) {
        return delegate.find(i);
    }

    public boolean isConnected(int i, int j) {
        return delegate.isConnected(i, j);
    }

    public int getPendingOperationCount() {
        return pendingOperations.size();
    }

    public void reset() {
        if (delegate instanceof UnionFind) {
            ((UnionFind) delegate).reset();
        }
        pendingOperations.clear();
        batchMode = false;
    }

    public static BatchUnionFind wrap(UnionFind uf) {
        return new BatchUnionFind(uf);
    }

    public static BatchUnionFind wrap(UnionFindDelegate delegate) {
        return new BatchUnionFind(delegate);
    }
}
