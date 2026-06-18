import java.util.ArrayList;
import java.util.List;

public class EnhancedOperationCounter {
    private long finds;
    private long unions;
    private List<OperationListener> listeners;
    private PerformanceMetrics metrics;

    public EnhancedOperationCounter() {
        this.finds = 0;
        this.unions = 0;
        this.listeners = new ArrayList<>();
        this.metrics = new PerformanceMetrics();
    }

    public void incrementFind(int index) {
        finds++;
        metrics.recordFind();
        notifyFindListeners(index);
    }

    public void incrementUnion(int index1, int index2) {
        unions++;
        metrics.recordUnion();
        notifyUnionListeners(index1, index2);
    }

    public void addListener(OperationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OperationListener listener) {
        listeners.remove(listener);
    }

    public long getFinds() {
        return finds;
    }

    public long getUnions() {
        return unions;
    }

    public PerformanceMetrics getMetrics() {
        return metrics;
    }

    public void reset() {
        finds = 0;
        unions = 0;
        metrics.reset();
    }

    public DisjointSet.Statistics toStatistics() {
        return new DisjointSet.Statistics(finds, unions);
    }

    private void notifyFindListeners(int index) {
        for (OperationListener listener : listeners) {
            listener.onFindOperation(index, finds);
        }
    }

    private void notifyUnionListeners(int index1, int index2) {
        for (OperationListener listener : listeners) {
            listener.onUnionOperation(index1, index2, unions);
        }
    }
}
