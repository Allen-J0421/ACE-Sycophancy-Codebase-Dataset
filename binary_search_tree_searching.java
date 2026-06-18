import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.Serializable;

// Performance profiling
class OperationProfile {
    public final String operationName;
    public final long durationNs;
    public final long timestamp;
    public final String phase;

    public OperationProfile(String operationName, long durationNs, String phase) {
        this.operationName = operationName;
        this.durationNs = durationNs;
        this.timestamp = System.currentTimeMillis();
        this.phase = phase;
    }

    public double getDurationMs() {
        return durationNs / 1_000_000.0;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]: %.4fms", operationName, phase, getDurationMs());
    }
}

class PerformanceProfiler {
    private final List<OperationProfile> profiles;
    private final Map<String, List<Long>> operationDurations;

    public PerformanceProfiler() {
        this.profiles = new ArrayList<>();
        this.operationDurations = new HashMap<>();
    }

    public void recordOperation(String operationName, long durationNs, String phase) {
        OperationProfile profile = new OperationProfile(operationName, durationNs, phase);
        profiles.add(profile);
        operationDurations.computeIfAbsent(operationName, k -> new ArrayList<>()).add(durationNs);
    }

    public List<OperationProfile> getProfiles() {
        return Collections.unmodifiableList(profiles);
    }

    public double getAverageDurationMs(String operationName) {
        List<Long> durations = operationDurations.get(operationName);
        if (durations == null || durations.isEmpty()) return 0;
        return durations.stream().mapToLong(Long::longValue).average().orElse(0) / 1_000_000.0;
    }

    public PerformanceReport getReport() {
        return new PerformanceReport(profiles, operationDurations);
    }

    public void clear() {
        profiles.clear();
        operationDurations.clear();
    }
}

class PerformanceReport {
    public final List<OperationProfile> profiles;
    public final Map<String, Double> averageDurationsByOperation;
    public final long totalProfiledTimeNs;

    public PerformanceReport(List<OperationProfile> profiles, Map<String, List<Long>> durations) {
        this.profiles = Collections.unmodifiableList(profiles);
        this.totalProfiledTimeNs = profiles.stream().mapToLong(p -> p.durationNs).sum();
        this.averageDurationsByOperation = new HashMap<>();

        durations.forEach((op, durs) -> {
            double avgMs = durs.stream().mapToLong(Long::longValue).average().orElse(0) / 1_000_000.0;
            averageDurationsByOperation.put(op, avgMs);
        });
    }

    @Override
    public String toString() {
        return String.format("PerformanceReport{totalProfiled=%.2fms, operations=%d}",
                           totalProfiledTimeNs / 1_000_000.0, profiles.size());
    }
}

// Query builder for complex operations
class TreeQuery<T extends Comparable<T>> {
    private final BinarySearchTree<T> tree;
    private List<T> results;
    private boolean executed;

    public TreeQuery(BinarySearchTree<T> tree) {
        this.tree = tree;
        this.results = new ArrayList<>();
        this.executed = false;
    }

    public TreeQuery<T> filter(FilterPredicate<T> predicate) {
        if (executed) throw new BSTOperationException("Query already executed");
        List<T> values = tree.stream();
        results = values.stream()
            .filter(predicate::test)
            .collect(ArrayList::new, List::add, List::addAll);
        return this;
    }

    public TreeQuery<T> inRange(T min, T max) {
        if (executed) throw new BSTOperationException("Query already executed");
        results = tree.getRange(min, max);
        return this;
    }

    public List<T> execute() {
        executed = true;
        return Collections.unmodifiableList(results);
    }

    public int count() {
        return execute().size();
    }

    public Optional<T> first() {
        List<T> res = execute();
        return res.isEmpty() ? Optional.empty() : Optional.of(res.get(0));
    }

    public Optional<T> last() {
        List<T> res = execute();
        return res.isEmpty() ? Optional.empty() : Optional.of(res.get(res.size() - 1));
    }
}

// Export formats
class TreeExport {
    public static <T extends Comparable<T>> String toJSON(BinarySearchTree<T> tree) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"type\": \"BinarySearchTree\",\n");
        sb.append("  \"size\": ").append(tree.size()).append(",\n");
        sb.append("  \"values\": ").append(tree.stream()).append(",\n");
        sb.append("  \"metrics\": {\n");
        TreeMetrics metrics = tree.getAnalyzer().getMetrics();
        sb.append("    \"height\": ").append(metrics.height).append(",\n");
        sb.append("    \"balanced\": ").append(metrics.isBalanced).append(",\n");
        sb.append("    \"balanceFactor\": ").append(metrics.balanceFactor).append("\n");
        sb.append("  }\n");
        sb.append("}");
        return sb.toString();
    }

    public static <T extends Comparable<T>> String toDOT(BinarySearchTree<T> tree) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph BST {\n");
        sb.append("  rankdir=TB;\n");
        sb.append("  node [shape=circle, style=filled, fillcolor=lightblue];\n");

        toDOTRecursive(tree.getRoot(), sb, new int[]{0});

        sb.append("}\n");
        return sb.toString();
    }

    private static <T extends Comparable<T>> void toDOTRecursive(Node<T> node, StringBuilder sb, int[] id) {
        if (node == null) return;

        int nodeId = id[0]++;
        sb.append("  node").append(nodeId).append(" [label=\"").append(node.value).append("\"];\n");

        if (node.left != null) {
            int leftId = id[0];
            toDOTRecursive(node.left, sb, id);
            sb.append("  node").append(nodeId).append(" -> node").append(leftId).append(";\n");
        }

        if (node.right != null) {
            int rightId = id[0];
            toDOTRecursive(node.right, sb, id);
            sb.append("  node").append(nodeId).append(" -> node").append(rightId).append(";\n");
        }
    }

    public static <T extends Comparable<T>> String toCSV(BinarySearchTree<T> tree) {
        StringBuilder sb = new StringBuilder();
        sb.append("value,rank,path_length\n");
        List<T> values = tree.stream();
        for (T value : values) {
            int rank = tree.getRank(value);
            int pathLen = tree.getPath(value).length;
            sb.append(value).append(",").append(rank).append(",").append(pathLen).append("\n");
        }
        return sb.toString();
    }
}

// Transaction support
class TreeTransaction<T extends Comparable<T>> {
    private final BinarySearchTree<T> tree;
    private final List<TreeSnapshot<T>> snapshots;
    private boolean committed;

    public TreeTransaction(BinarySearchTree<T> tree) {
        this.tree = tree;
        this.snapshots = new ArrayList<>();
        this.snapshots.add(tree.snapshot());
        this.committed = false;
    }

    public void execute(Consumer<BinarySearchTree<T>> operation) {
        if (committed) throw new BSTOperationException("Transaction already committed");
        operation.accept(tree);
    }

    public void commit() {
        committed = true;
        snapshots.add(tree.snapshot());
    }

    public void rollback() {
        if (snapshots.isEmpty()) {
            throw new BSTOperationException("Cannot rollback: no states recorded");
        }
        TreeSnapshot<T> initial = snapshots.get(0);
        tree.restore(initial);
        committed = false;
        snapshots.clear();
        snapshots.add(initial);
    }

    public boolean isCommitted() {
        return committed;
    }

    @Override
    public String toString() {
        return String.format("Transaction{snapshots=%d, committed=%s}", snapshots.size(), committed);
    }
}

// Path and LCA operations
class TreePath<T extends Comparable<T>> {
    public final List<T> nodes;
    public final int length;

    public TreePath(List<T> nodes) {
        this.nodes = Collections.unmodifiableList(new ArrayList<>(nodes));
        this.length = nodes.size();
    }

    @Override
    public String toString() {
        return String.format("Path{length=%d, nodes=%s}", length, nodes);
    }
}

class LCAResult<T extends Comparable<T>> {
    public final Optional<T> ancestor;
    public final TreePath<T> pathToAncestor1;
    public final TreePath<T> pathToAncestor2;
    public final int distance;

    public LCAResult(Optional<T> ancestor, TreePath<T> path1, TreePath<T> path2, int distance) {
        this.ancestor = ancestor;
        this.pathToAncestor1 = path1;
        this.pathToAncestor2 = path2;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return String.format("LCA{ancestor=%s, dist=%d}", ancestor, distance);
    }
}

class TreeVisualization {
    private StringBuilder sb;
    private int nodeCount;
    private int maxWidth;

    public TreeVisualization() {
        this.sb = new StringBuilder();
        this.nodeCount = 0;
    }

    public <T extends Comparable<T>> String visualize(Node<T> root) {
        sb = new StringBuilder();
        nodeCount = 0;
        maxWidth = 0;

        if (root == null) {
            return "[Empty Tree]";
        }

        visualizeRecursive(root, "", "", true);
        return sb.toString();
    }

    private <T extends Comparable<T>> void visualizeRecursive(Node<T> node, String prefix,
                                                              String childrenPrefix, boolean isTail) {
        if (node == null) return;

        nodeCount++;
        sb.append(prefix)
          .append(isTail ? "└── " : "├── ")
          .append(node.value)
          .append("\n");

        List<Node<T>> children = new ArrayList<>();
        if (node.left != null) children.add(node.left);
        if (node.right != null) children.add(node.right);

        for (int i = 0; i < children.size(); i++) {
            boolean isLast = (i == children.size() - 1);
            String extension = isTail ? "    " : "│   ";
            visualizeRecursive(children.get(i),
                             childrenPrefix + extension,
                             childrenPrefix + (isLast ? "    " : "│   "),
                             isLast);
        }
    }

    public String getStats() {
        return String.format("Nodes in visualization: %d", nodeCount);
    }
}

class MemoryEstimate {
    public final int nodeCount;
    public final long estimatedBytesPerNode;
    public final long totalEstimatedBytes;
    public final String humanReadable;

    public MemoryEstimate(int nodeCount, long estimatedBytesPerNode) {
        this.nodeCount = nodeCount;
        this.estimatedBytesPerNode = estimatedBytesPerNode;
        this.totalEstimatedBytes = nodeCount * estimatedBytesPerNode;
        this.humanReadable = formatBytes(totalEstimatedBytes);
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int unit = 1024;
        if (bytes < unit * 1024) return String.format("%.2f KB", (double) bytes / unit);
        unit *= 1024;
        if (bytes < unit * 1024) return String.format("%.2f MB", (double) bytes / unit);
        unit *= 1024;
        return String.format("%.2f GB", (double) bytes / unit);
    }

    @Override
    public String toString() {
        return String.format("Memory{nodes=%d, perNode=%d bytes, total=%s}", nodeCount, estimatedBytesPerNode, humanReadable);
    }
}

class SubtreeInfo<T extends Comparable<T>> {
    public final T root;
    public final int size;
    public final int height;
    public final List<T> values;

    public SubtreeInfo(T root, int size, int height, List<T> values) {
        this.root = root;
        this.size = size;
        this.height = height;
        this.values = Collections.unmodifiableList(values);
    }

    @Override
    public String toString() {
        return String.format("Subtree{root=%s, size=%d, height=%d}", root, size, height);
    }
}

// Custom Exceptions
class BSTException extends RuntimeException {
    public BSTException(String message) {
        super(message);
    }

    public BSTException(String message, Throwable cause) {
        super(message, cause);
    }
}

class BSTIntegrityException extends BSTException {
    public BSTIntegrityException(String message) {
        super(message);
    }
}

class BSTOperationException extends BSTException {
    public BSTOperationException(String message) {
        super(message);
    }

    public BSTOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

class Node<T extends Comparable<T>> implements Serializable {
    private static final long serialVersionUID = 1L;
    T value;
    Node<T> left;
    Node<T> right;

    public Node(T value) {
        this.value = value;
    }

    public Node<T> deepCopy() {
        Node<T> copy = new Node<>(value);
        if (left != null) copy.left = left.deepCopy();
        if (right != null) copy.right = right.deepCopy();
        return copy;
    }

    public int computeHeight() {
        int leftHeight = left == null ? 0 : left.computeHeight();
        int rightHeight = right == null ? 0 : right.computeHeight();
        return 1 + Math.max(leftHeight, rightHeight);
    }
}

class IntegrityCheckResult {
    public final boolean isValid;
    public final List<String> violations;
    public final int nodeCount;
    public final int violations() {
        return violations.size();
    }

    public IntegrityCheckResult(boolean isValid, List<String> violations, int nodeCount) {
        this.isValid = isValid;
        this.violations = Collections.unmodifiableList(violations);
        this.nodeCount = nodeCount;
    }

    @Override
    public String toString() {
        return String.format("IntegrityCheck{valid=%s, nodes=%d, violations=%d}", isValid, nodeCount, violations.size());
    }
}

class ChangeSet<T> {
    public enum Action { INSERT, DELETE, UPDATE }

    public static class Change<T> {
        public final Action action;
        public final T value;
        public final long timestamp;

        public Change(Action action, T value) {
            this.action = action;
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return String.format("Change{%s, value=%s, time=%d}", action, value, timestamp);
        }
    }

    private final List<Change<T>> changes;
    private final long createdAt;

    public ChangeSet() {
        this.changes = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
    }

    public void add(Action action, T value) {
        changes.add(new Change<>(action, value));
    }

    public List<Change<T>> getChanges() {
        return Collections.unmodifiableList(changes);
    }

    public int size() {
        return changes.size();
    }

    public boolean isEmpty() {
        return changes.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("ChangeSet{changes=%d, age=%dms}", changes.size(), System.currentTimeMillis() - createdAt);
    }
}

class TreeFilter<T extends Comparable<T>> {
    private final FilterPredicate<T> predicate;

    public TreeFilter(FilterPredicate<T> predicate) {
        this.predicate = predicate;
    }

    public List<T> apply(List<T> values) {
        List<T> result = new ArrayList<>();
        for (T value : values) {
            if (predicate.test(value)) {
                result.add(value);
            }
        }
        return result;
    }

    public static <T extends Comparable<T>> TreeFilter<T> greaterThan(T value) {
        return new TreeFilter<>(v -> v.compareTo(value) > 0);
    }

    public static <T extends Comparable<T>> TreeFilter<T> lessThan(T value) {
        return new TreeFilter<>(v -> v.compareTo(value) < 0);
    }

    public static <T extends Comparable<T>> TreeFilter<T> between(T min, T max) {
        return new TreeFilter<>(v -> v.compareTo(min) >= 0 && v.compareTo(max) <= 0);
    }
}

interface FilterPredicate<T> {
    boolean test(T value);
}

class TreeConfiguration {
    public final boolean enableEventLogging;
    public final boolean enableMetrics;
    public final boolean enableCaching;
    public final int cacheSize;
    public final boolean threadSafe;

    private TreeConfiguration(Builder builder) {
        this.enableEventLogging = builder.enableEventLogging;
        this.enableMetrics = builder.enableMetrics;
        this.enableCaching = builder.enableCaching;
        this.cacheSize = builder.cacheSize;
        this.threadSafe = builder.threadSafe;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static TreeConfiguration defaultConfig() {
        return builder().build();
    }

    public static class Builder {
        private boolean enableEventLogging = true;
        private boolean enableMetrics = true;
        private boolean enableCaching = false;
        private int cacheSize = 100;
        private boolean threadSafe = false;

        public Builder enableEventLogging(boolean enable) {
            this.enableEventLogging = enable;
            return this;
        }

        public Builder enableMetrics(boolean enable) {
            this.enableMetrics = enable;
            return this;
        }

        public Builder enableCaching(boolean enable) {
            this.enableCaching = enable;
            return this;
        }

        public Builder cacheSize(int size) {
            this.cacheSize = Math.max(10, size);
            return this;
        }

        public Builder threadSafe(boolean safe) {
            this.threadSafe = safe;
            return this;
        }

        public TreeConfiguration build() {
            return new TreeConfiguration(this);
        }
    }
}

class QueryCache<T extends Comparable<T>> {
    private static class CacheEntry<T> {
        final T query;
        final Object result;
        long accessTime;

        CacheEntry(T query, Object result) {
            this.query = query;
            this.result = result;
            this.accessTime = System.nanoTime();
        }
    }

    private final int maxSize;
    private final LinkedHashMap<T, CacheEntry<T>> cache;

    public QueryCache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<T, CacheEntry<T>>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<T, CacheEntry<T>> eldest) {
                return size() > maxSize;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public <R> Optional<R> get(T query) {
        CacheEntry<T> entry = cache.get(query);
        if (entry != null) {
            entry.accessTime = System.nanoTime();
            return Optional.of((R) entry.result);
        }
        return Optional.empty();
    }

    public <R> void put(T query, R result) {
        cache.put(query, new CacheEntry<>(query, result));
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }

    public CacheStatistics getStats() {
        return new CacheStatistics(cache.size(), maxSize);
    }
}

class CacheStatistics {
    public final int entries;
    public final int maxSize;
    public final double utilization;

    public CacheStatistics(int entries, int maxSize) {
        this.entries = entries;
        this.maxSize = maxSize;
        this.utilization = maxSize > 0 ? (double) entries / maxSize : 0;
    }

    @Override
    public String toString() {
        return String.format("CacheStats{entries=%d, max=%d, util=%.1f%%}", entries, maxSize, utilization * 100);
    }
}

class TreeStatistics<T extends Comparable<T>> {
    public final int nodeCount;
    public final int leafCount;
    public final int internalNodeCount;
    public final double avgDepth;
    public final double avgBranchingFactor;
    public final List<T> sortedValues;

    public TreeStatistics(int nodeCount, int leafCount, int internalNodeCount,
                         double avgDepth, double avgBranchingFactor, List<T> sortedValues) {
        this.nodeCount = nodeCount;
        this.leafCount = leafCount;
        this.internalNodeCount = internalNodeCount;
        this.avgDepth = avgDepth;
        this.avgBranchingFactor = avgBranchingFactor;
        this.sortedValues = Collections.unmodifiableList(sortedValues);
    }

    @Override
    public String toString() {
        return String.format("TreeStatistics{nodes=%d, leaves=%d, internal=%d, avgDepth=%.2f, branching=%.2f}",
                           nodeCount, leafCount, internalNodeCount, avgDepth, avgBranchingFactor);
    }
}

class TreeDifference<T extends Comparable<T>> {
    public final List<T> onlyInFirst;
    public final List<T> onlyInSecond;
    public final List<T> common;

    public TreeDifference(List<T> onlyInFirst, List<T> onlyInSecond, List<T> common) {
        this.onlyInFirst = Collections.unmodifiableList(onlyInFirst);
        this.onlyInSecond = Collections.unmodifiableList(onlyInSecond);
        this.common = Collections.unmodifiableList(common);
    }

    public boolean isEqual() {
        return onlyInFirst.isEmpty() && onlyInSecond.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("TreeDifference{only1st=%d, only2nd=%d, common=%d, equal=%s}",
                           onlyInFirst.size(), onlyInSecond.size(), common.size(), isEqual());
    }
}

class BatchOperationResult<T> {
    public final int successCount;
    public final int failureCount;
    public final List<T> processedItems;
    public final long executionTimeMs;

    public BatchOperationResult(int successCount, int failureCount, List<T> processedItems, long executionTimeMs) {
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.processedItems = Collections.unmodifiableList(processedItems);
        this.executionTimeMs = executionTimeMs;
    }

    public double successRate() {
        int total = successCount + failureCount;
        return total > 0 ? (double) successCount / total : 0;
    }

    @Override
    public String toString() {
        return String.format("BatchResult{success=%d, failed=%d, rate=%.1f%%, timeMs=%d}",
                           successCount, failureCount, successRate() * 100, executionTimeMs);
    }
}

interface TreeEventListener<T> {
    void onInsert(T value);
    void onDelete(T value);
    void onClear();
}

class TreeEvent<T> {
    enum Type { INSERT, DELETE, CLEAR, REBALANCE }
    public final Type type;
    public final T value;
    public final long timestamp;

    public TreeEvent(Type type, T value) {
        this.type = type;
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return String.format("TreeEvent{type=%s, value=%s, time=%d}", type, value, timestamp);
    }
}

class OperationMetrics {
    public final long insertCount;
    public final long deleteCount;
    public final long searchCount;
    public final long rangeQueryCount;
    public final double avgSearchTimeMs;
    public final double avgInsertTimeMs;

    public OperationMetrics(long insertCount, long deleteCount, long searchCount,
                           long rangeQueryCount, double avgSearchTimeMs, double avgInsertTimeMs) {
        this.insertCount = insertCount;
        this.deleteCount = deleteCount;
        this.searchCount = searchCount;
        this.rangeQueryCount = rangeQueryCount;
        this.avgSearchTimeMs = avgSearchTimeMs;
        this.avgInsertTimeMs = avgInsertTimeMs;
    }

    @Override
    public String toString() {
        return String.format("OperationMetrics{inserts=%d, deletes=%d, searches=%d, ranges=%d, avgSearch=%.2fms, avgInsert=%.2fms}",
                           insertCount, deleteCount, searchCount, rangeQueryCount, avgSearchTimeMs, avgInsertTimeMs);
    }
}

class SearchResult<T> {
    public final T value;
    public final List<Integer> path;
    public final int depth;
    public final long searchTimeNs;
    public final boolean found;

    public SearchResult(T value, List<Integer> path, int depth, long searchTimeNs, boolean found) {
        this.value = value;
        this.path = Collections.unmodifiableList(path);
        this.depth = depth;
        this.searchTimeNs = searchTimeNs;
        this.found = found;
    }

    @Override
    public String toString() {
        return String.format("SearchResult{value=%s, found=%s, depth=%d, pathLength=%d, timeNs=%d}",
                           value, found, depth, path.size(), searchTimeNs);
    }
}

class TreeSnapshot<T extends Comparable<T>> {
    public final List<T> values;
    public final TreeMetrics metrics;
    public final long timestamp;

    public TreeSnapshot(List<T> values, TreeMetrics metrics) {
        this.values = Collections.unmodifiableList(new ArrayList<>(values));
        this.metrics = metrics;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean hasChanged(List<T> currentValues) {
        return !this.values.equals(currentValues);
    }

    @Override
    public String toString() {
        return String.format("TreeSnapshot{size=%d, timestamp=%d, %s}", values.size(), timestamp, metrics);
    }
}

interface NodeVisitor<T extends Comparable<T>, R> {
    R visit(Node<T> node);
}

interface Restorable<T extends Comparable<T>> {
    void restore(TreeSnapshot<T> snapshot);
    TreeSnapshot<T> snapshot();
}

class TreeMetrics {
    public final int height;
    public final int size;
    public final double balanceFactor;
    public final boolean isBalanced;
    public final int minDepth;
    public final int maxDepth;

    public TreeMetrics(int height, int size, double balanceFactor, boolean isBalanced,
                      int minDepth, int maxDepth) {
        this.height = height;
        this.size = size;
        this.balanceFactor = balanceFactor;
        this.isBalanced = isBalanced;
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
    }

    @Override
    public String toString() {
        return String.format("TreeMetrics{height=%d, size=%d, balance=%.2f, balanced=%s, depths=[%d,%d]}",
                           height, size, balanceFactor, isBalanced, minDepth, maxDepth);
    }
}

interface TreeTraversal<T> {
    List<T> traverse();
}

abstract class AbstractTraversal<T extends Comparable<T>> implements TreeTraversal<T> {
    protected Node<T> root;

    public AbstractTraversal(Node<T> root) {
        this.root = root;
    }

    protected void processNode(Node<T> node, List<T> result) {
        if (node != null) result.add(node.value);
    }
}

class InOrderTraversal<T extends Comparable<T>> extends AbstractTraversal<T> {
    public InOrderTraversal(Node<T> root) {
        super(root);
    }

    @Override
    public List<T> traverse() {
        List<T> result = new ArrayList<>();
        traverseRecursive(root, result);
        return result;
    }

    private void traverseRecursive(Node<T> node, List<T> result) {
        if (node == null) return;
        traverseRecursive(node.left, result);
        processNode(node, result);
        traverseRecursive(node.right, result);
    }
}

class PreOrderTraversal<T extends Comparable<T>> extends AbstractTraversal<T> {
    public PreOrderTraversal(Node<T> root) {
        super(root);
    }

    @Override
    public List<T> traverse() {
        List<T> result = new ArrayList<>();
        traverseRecursive(root, result);
        return result;
    }

    private void traverseRecursive(Node<T> node, List<T> result) {
        if (node == null) return;
        processNode(node, result);
        traverseRecursive(node.left, result);
        traverseRecursive(node.right, result);
    }
}

class PostOrderTraversal<T extends Comparable<T>> extends AbstractTraversal<T> {
    public PostOrderTraversal(Node<T> root) {
        super(root);
    }

    @Override
    public List<T> traverse() {
        List<T> result = new ArrayList<>();
        traverseRecursive(root, result);
        return result;
    }

    private void traverseRecursive(Node<T> node, List<T> result) {
        if (node == null) return;
        traverseRecursive(node.left, result);
        traverseRecursive(node.right, result);
        processNode(node, result);
    }
}

class LevelOrderTraversal<T extends Comparable<T>> extends AbstractTraversal<T> {
    public LevelOrderTraversal(Node<T> root) {
        super(root);
    }

    @Override
    public List<T> traverse() {
        List<T> result = new ArrayList<>();
        if (root == null) return result;

        Queue<Node<T>> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Node<T> node = queue.poll();
            processNode(node, result);

            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
        return result;
    }
}

class TreeValidator<T extends Comparable<T>> {
    public boolean isValidBST(Node<T> root) {
        return isValidBSTRecursive(root, null, null);
    }

    private boolean isValidBSTRecursive(Node<T> node, T min, T max) {
        if (node == null) return true;

        if (min != null && node.value.compareTo(min) <= 0) return false;
        if (max != null && node.value.compareTo(max) >= 0) return false;

        return isValidBSTRecursive(node.left, min, node.value) &&
               isValidBSTRecursive(node.right, node.value, max);
    }

    public IntegrityCheckResult performFullIntegrityCheck(Node<T> root) {
        List<String> violations = new ArrayList<>();
        int[] nodeCount = {0};

        performIntegrityCheckRecursive(root, null, null, violations, nodeCount);

        return new IntegrityCheckResult(violations.isEmpty(), violations, nodeCount[0]);
    }

    private void performIntegrityCheckRecursive(Node<T> node, T min, T max,
                                               List<String> violations, int[] nodeCount) {
        if (node == null) return;

        nodeCount[0]++;

        if (min != null && node.value.compareTo(min) <= 0) {
            violations.add("Node " + node.value + " violates min bound " + min);
        }
        if (max != null && node.value.compareTo(max) >= 0) {
            violations.add("Node " + node.value + " violates max bound " + max);
        }

        performIntegrityCheckRecursive(node.left, min, node.value, violations, nodeCount);
        performIntegrityCheckRecursive(node.right, node.value, max, violations, nodeCount);
    }

    public String getValidationReport(Node<T> root) {
        IntegrityCheckResult result = performFullIntegrityCheck(root);
        return String.format("BST Validation: %s (nodes=%d, violations=%d)",
                           result.isValid ? "VALID" : "INVALID", result.nodeCount, result.violations());
    }
}

class TreeAnalyzer<T extends Comparable<T>> {
    private Node<T> root;
    private Map<String, Object> cache;

    public TreeAnalyzer(Node<T> root) {
        this.root = root;
        this.cache = new HashMap<>();
    }

    public TreeMetrics getMetrics() {
        int height = getHeight();
        int size = getSize();
        DepthInfo depthInfo = getDepthInfo();
        double balanceFactor = calculateBalanceFactor();
        boolean isBalanced = isBalanced();

        return new TreeMetrics(height, size, balanceFactor, isBalanced,
                              depthInfo.minDepth, depthInfo.maxDepth);
    }

    public int getHeight() {
        return getHeightRecursive(root);
    }

    private int getHeightRecursive(Node<T> node) {
        return node == null ? -1 : 1 + Math.max(getHeightRecursive(node.left),
                                                 getHeightRecursive(node.right));
    }

    public int getSize() {
        return getSizeRecursive(root);
    }

    private int getSizeRecursive(Node<T> node) {
        return node == null ? 0 : 1 + getSizeRecursive(node.left) + getSizeRecursive(node.right);
    }

    public boolean isBalanced() {
        return isBalancedRecursive(root).isBalanced;
    }

    private BalanceInfo isBalancedRecursive(Node<T> node) {
        if (node == null) return new BalanceInfo(true, -1);

        BalanceInfo leftInfo = isBalancedRecursive(node.left);
        if (!leftInfo.isBalanced) return new BalanceInfo(false, 0);

        BalanceInfo rightInfo = isBalancedRecursive(node.right);
        if (!rightInfo.isBalanced) return new BalanceInfo(false, 0);

        int heightDiff = Math.abs(leftInfo.height - rightInfo.height);
        boolean balanced = heightDiff <= 1;
        int height = 1 + Math.max(leftInfo.height, rightInfo.height);

        return new BalanceInfo(balanced, height);
    }

    private DepthInfo getDepthInfo() {
        DepthInfo info = new DepthInfo();
        calculateDepthRecursive(root, 0, info);
        return info;
    }

    private void calculateDepthRecursive(Node<T> node, int depth, DepthInfo info) {
        if (node == null) return;

        if (node.left == null && node.right == null) {
            info.minDepth = Math.min(info.minDepth == Integer.MAX_VALUE ? depth : info.minDepth, depth);
            info.maxDepth = Math.max(info.maxDepth, depth);
        }

        calculateDepthRecursive(node.left, depth + 1, info);
        calculateDepthRecursive(node.right, depth + 1, info);
    }

    private double calculateBalanceFactor() {
        BalanceFactor bf = getBalanceFactorRecursive(root);
        return bf.totalDiff > 0 ? (double) bf.balancedCount / bf.totalDiff : 1.0;
    }

    private BalanceFactor getBalanceFactorRecursive(Node<T> node) {
        if (node == null) return new BalanceFactor(0, 0);

        BalanceFactor left = getBalanceFactorRecursive(node.left);
        BalanceFactor right = getBalanceFactorRecursive(node.right);

        int diff = Math.abs(left.totalDiff - right.totalDiff);
        int balanced = left.balancedCount + right.balancedCount + (diff <= 1 ? 1 : 0);

        return new BalanceFactor(diff + left.totalDiff + right.totalDiff, balanced);
    }

    private static class BalanceInfo {
        boolean isBalanced;
        int height;

        BalanceInfo(boolean isBalanced, int height) {
            this.isBalanced = isBalanced;
            this.height = height;
        }
    }

    private static class DepthInfo {
        int minDepth = Integer.MAX_VALUE;
        int maxDepth = 0;
    }

    private static class BalanceFactor {
        int totalDiff;
        int balancedCount;

        BalanceFactor(int totalDiff, int balancedCount) {
            this.totalDiff = totalDiff;
            this.balancedCount = balancedCount;
        }
    }
}

interface TreeOperations<T extends Comparable<T>> {
    void insert(T value);
    boolean search(T key);
    boolean delete(T key);
    T getMin();
    T getMax();
    Optional<T> getFloor(T key);
    Optional<T> getCeiling(T key);
    Optional<T> getSuccessor(T key);
    Optional<T> getPredecessor(T key);
    int getRank(T key);
    List<T> getRange(T min, T max);
    int countInRange(T min, T max);
    boolean containsAll(T... values);
    int size();
    boolean isEmpty();
    void clear();
}

class BinarySearchTree<T extends Comparable<T>> implements TreeOperations<T>, Iterable<T>, Restorable<T> {
    private Node<T> root;
    private int size;
    private final TreeConfiguration config;
    private final List<TreeEventListener<T>> listeners;
    private final List<TreeEvent<T>> eventLog;
    private final QueryCache<T> queryCache;
    private final ChangeSet<T> changeSet;
    private final PerformanceProfiler profiler;
    private long totalInsertTimeNs;
    private long totalSearchTimeNs;
    private long insertCount;
    private long searchCount;
    private long deleteCount;
    private long rangeQueryCount;
    private long cacheHits;
    private long cacheMisses;
    private int operationsSinceValidation;
    private static final int VALIDATION_FREQUENCY = 100;

    public BinarySearchTree() {
        this(TreeConfiguration.defaultConfig());
    }

    public BinarySearchTree(TreeConfiguration config) {
        this.root = null;
        this.size = 0;
        this.config = config;
        this.listeners = new CopyOnWriteArrayList<>();
        this.eventLog = config.enableEventLogging ? new ArrayList<>() : null;
        this.queryCache = config.enableCaching ? new QueryCache<>(config.cacheSize) : null;
        this.changeSet = new ChangeSet<>();
        this.profiler = new PerformanceProfiler();
        this.operationsSinceValidation = 0;
    }

    public void addListener(TreeEventListener<T> listener) {
        listeners.add(listener);
    }

    public void removeListener(TreeEventListener<T> listener) {
        listeners.remove(listener);
    }

    private void fireInsertEvent(T value) {
        if (!config.enableEventLogging) return;
        TreeEvent<T> event = new TreeEvent<>(TreeEvent.Type.INSERT, value);
        eventLog.add(event);
        for (TreeEventListener<T> listener : listeners) {
            listener.onInsert(value);
        }
    }

    private void fireDeleteEvent(T value) {
        if (!config.enableEventLogging) return;
        TreeEvent<T> event = new TreeEvent<>(TreeEvent.Type.DELETE, value);
        eventLog.add(event);
        for (TreeEventListener<T> listener : listeners) {
            listener.onDelete(value);
        }
    }

    private void fireClearEvent() {
        if (!config.enableEventLogging) return;
        TreeEvent<T> event = new TreeEvent<>(TreeEvent.Type.CLEAR, null);
        eventLog.add(event);
        for (TreeEventListener<T> listener : listeners) {
            listener.onClear();
        }
    }

    public static <T extends Comparable<T>> BSTBuilder<T> builder() {
        return new BSTBuilder<>();
    }

    @Override
    public void insert(T value) {
        if (value == null) throw new BSTOperationException("Value cannot be null");
        try {
            long startNs = config.enableMetrics ? System.nanoTime() : 0;
            int oldSize = size;
            root = insertRecursive(root, value);
            if (size > oldSize) {
                if (config.enableMetrics) {
                    totalInsertTimeNs += System.nanoTime() - startNs;
                    insertCount++;
                }
                if (config.enableCaching) queryCache.clear();
                changeSet.add(ChangeSet.Action.INSERT, value);
                fireInsertEvent(value);
                operationsSinceValidation++;
                if (operationsSinceValidation >= VALIDATION_FREQUENCY) {
                    validateIntegrity();
                    operationsSinceValidation = 0;
                }
            }
        } catch (Exception e) {
            throw new BSTOperationException("Insert failed for value: " + value, e);
        }
    }

    public BatchOperationResult<T> insertBatch(T... values) {
        long startMs = System.currentTimeMillis();
        int success = 0;
        int failed = 0;
        List<T> processed = new ArrayList<>();

        for (T value : values) {
            try {
                insert(value);
                success++;
                processed.add(value);
            } catch (Exception e) {
                failed++;
            }
        }

        long executionTimeMs = System.currentTimeMillis() - startMs;
        return new BatchOperationResult<>(success, failed, processed, executionTimeMs);
    }

    private Node<T> insertRecursive(Node<T> node, T value) {
        if (node == null) {
            size++;
            return new Node<>(value);
        }

        int comparison = value.compareTo(node.value);
        if (comparison < 0) {
            node.left = insertRecursive(node.left, value);
        } else if (comparison > 0) {
            node.right = insertRecursive(node.right, value);
        }
        return node;
    }

    @Override
    public boolean search(T key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        if (config.enableCaching) {
            Optional<Boolean> cached = queryCache.get(key);
            if (cached.isPresent()) {
                cacheHits++;
                return cached.get();
            }
            cacheMisses++;
        }

        long startNs = config.enableMetrics ? System.nanoTime() : 0;
        List<Integer> path = new ArrayList<>();
        boolean found = searchWithPath(root, key, path);

        if (config.enableMetrics) {
            long timeNs = System.nanoTime() - startNs;
            totalSearchTimeNs += timeNs;
            searchCount++;
        }

        if (config.enableCaching) {
            queryCache.put(key, found);
        }
        return found;
    }

    public SearchResult<T> searchDetailed(T key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        long startNs = config.enableMetrics ? System.nanoTime() : 0;
        List<Integer> path = new ArrayList<>();
        boolean found = searchWithPath(root, key, path);
        long timeNs = config.enableMetrics ? System.nanoTime() - startNs : 0;

        if (config.enableMetrics) {
            totalSearchTimeNs += timeNs;
            searchCount++;
        }

        return new SearchResult<>(key, path, path.size(), timeNs, found);
    }

    private boolean searchWithPath(Node<T> node, T key, List<Integer> path) {
        if (node == null) return false;

        int comparison = key.compareTo(node.value);
        if (comparison == 0) {
            path.add(0);
            return true;
        } else if (comparison < 0) {
            boolean found = searchWithPath(node.left, key, path);
            if (found) path.add(-1);
            return found;
        } else {
            boolean found = searchWithPath(node.right, key, path);
            if (found) path.add(1);
            return found;
        }
    }

    private boolean searchRecursive(Node<T> node, T key) {
        if (node == null) return false;

        int comparison = key.compareTo(node.value);
        if (comparison == 0) return true;
        else if (comparison < 0) return searchRecursive(node.left, key);
        else return searchRecursive(node.right, key);
    }

    @Override
    public boolean delete(T key) {
        if (key == null) throw new BSTOperationException("Key cannot be null");
        try {
            int oldSize = size;
            root = deleteRecursive(root, key);
            if (size < oldSize) {
                if (config.enableMetrics) deleteCount++;
                if (config.enableCaching) queryCache.clear();
                changeSet.add(ChangeSet.Action.DELETE, key);
                fireDeleteEvent(key);
                operationsSinceValidation++;
                if (operationsSinceValidation >= VALIDATION_FREQUENCY) {
                    validateIntegrity();
                    operationsSinceValidation = 0;
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new BSTOperationException("Delete failed for key: " + key, e);
        }
    }

    public BatchOperationResult<T> deleteBatch(T... values) {
        long startMs = System.currentTimeMillis();
        int success = 0;
        int failed = 0;
        List<T> processed = new ArrayList<>();

        for (T value : values) {
            try {
                if (delete(value)) {
                    success++;
                } else {
                    failed++;
                }
                processed.add(value);
            } catch (Exception e) {
                failed++;
            }
        }

        long executionTimeMs = System.currentTimeMillis() - startMs;
        return new BatchOperationResult<>(success, failed, processed, executionTimeMs);
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
        if (eventLog != null) eventLog.clear();
        if (queryCache != null) queryCache.clear();
        fireClearEvent();
    }

    private Node<T> deleteRecursive(Node<T> node, T key) {
        if (node == null) return null;

        int comparison = key.compareTo(node.value);
        if (comparison < 0) {
            node.left = deleteRecursive(node.left, key);
        } else if (comparison > 0) {
            node.right = deleteRecursive(node.right, key);
        } else {
            size--;
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;

            Node<T> minRight = findMinNode(node.right);
            node.value = minRight.value;
            node.right = deleteRecursive(node.right, minRight.value);
            size++;
        }
        return node;
    }

    @Override
    public T getMin() {
        if (isEmpty()) throw new IllegalStateException("Tree is empty");
        return findMinNode(root).value;
    }

    @Override
    public T getMax() {
        if (isEmpty()) throw new IllegalStateException("Tree is empty");
        return findMaxNode(root).value;
    }

    @Override
    public Optional<T> getFloor(T key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        return Optional.ofNullable(getFloorRecursive(root, key));
    }

    private T getFloorRecursive(Node<T> node, T key) {
        if (node == null) return null;

        if (key.compareTo(node.value) == 0) return node.value;

        if (key.compareTo(node.value) < 0) {
            return getFloorRecursive(node.left, key);
        }

        T floor = getFloorRecursive(node.right, key);
        return floor != null ? floor : node.value;
    }

    @Override
    public Optional<T> getCeiling(T key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        return Optional.ofNullable(getCeilingRecursive(root, key));
    }

    private T getCeilingRecursive(Node<T> node, T key) {
        if (node == null) return null;

        if (key.compareTo(node.value) == 0) return node.value;

        if (key.compareTo(node.value) > 0) {
            return getCeilingRecursive(node.right, key);
        }

        T ceiling = getCeilingRecursive(node.left, key);
        return ceiling != null ? ceiling : node.value;
    }

    @Override
    public Optional<T> getSuccessor(T key) {
        Optional<T> ceiling = getCeiling(key);
        if (!ceiling.isPresent()) return Optional.empty();

        T ceil = ceiling.get();
        if (ceil.compareTo(key) == 0) {
            return Optional.ofNullable(getSuccessorRecursive(root, key));
        }
        return Optional.of(ceil);
    }

    private T getSuccessorRecursive(Node<T> node, T key) {
        if (node == null) return null;

        if (key.compareTo(node.value) < 0) {
            T left = getSuccessorRecursive(node.left, key);
            return left != null ? left : node.value;
        }
        return getSuccessorRecursive(node.right, key);
    }

    @Override
    public Optional<T> getPredecessor(T key) {
        Optional<T> floor = getFloor(key);
        if (!floor.isPresent()) return Optional.empty();

        T flr = floor.get();
        if (flr.compareTo(key) == 0) {
            return Optional.ofNullable(getPredecessorRecursive(root, key));
        }
        return Optional.of(flr);
    }

    private T getPredecessorRecursive(Node<T> node, T key) {
        if (node == null) return null;

        if (key.compareTo(node.value) > 0) {
            T right = getPredecessorRecursive(node.right, key);
            return right != null ? right : node.value;
        }
        return getPredecessorRecursive(node.left, key);
    }

    @Override
    public int getRank(T key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        return getRankRecursive(root, key);
    }

    private int getRankRecursive(Node<T> node, T key) {
        if (node == null) return 0;

        int comparison = key.compareTo(node.value);
        if (comparison < 0) {
            return getRankRecursive(node.left, key);
        } else if (comparison > 0) {
            return 1 + getSizeRecursive(node.left) + getRankRecursive(node.right, key);
        } else {
            return getSizeRecursive(node.left);
        }
    }

    @Override
    public List<T> getRange(T min, T max) {
        if (min == null || max == null) throw new IllegalArgumentException("Range bounds cannot be null");
        if (min.compareTo(max) > 0) throw new IllegalArgumentException("Min cannot be greater than max");

        long startNs = System.nanoTime();
        List<T> result = new ArrayList<>();
        getRangeRecursive(root, min, max, result);
        totalSearchTimeNs += System.nanoTime() - startNs;
        rangeQueryCount++;
        return result;
    }

    private void getRangeRecursive(Node<T> node, T min, T max, List<T> result) {
        if (node == null) return;

        int cmpMin = node.value.compareTo(min);
        int cmpMax = node.value.compareTo(max);

        if (cmpMin >= 0) getRangeRecursive(node.left, min, max, result);
        if (cmpMin >= 0 && cmpMax <= 0) result.add(node.value);
        if (cmpMax <= 0) getRangeRecursive(node.right, min, max, result);
    }

    @Override
    public int countInRange(T min, T max) {
        return getRange(min, max).size();
    }

    @Override
    public boolean containsAll(T... values) {
        if (values == null) return false;
        for (T value : values) {
            if (!search(value)) return false;
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new InOrderTraversal<>(root).traverse().iterator();
    }

    private Node<T> findMinNode(Node<T> node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private Node<T> findMaxNode(Node<T> node) {
        while (node.right != null) node = node.right;
        return node;
    }

    private int getSizeRecursive(Node<T> node) {
        return node == null ? 0 : 1 + getSizeRecursive(node.left) + getSizeRecursive(node.right);
    }

    public Node<T> getRoot() {
        return root;
    }

    public List<T> traverse(TreeTraversal<T> traversal) {
        return traversal.traverse();
    }

    public TreeValidator<T> getValidator() {
        return new TreeValidator<>();
    }

    public TreeAnalyzer<T> getAnalyzer() {
        return new TreeAnalyzer<>(root);
    }

    public <R> R accept(NodeVisitor<T, R> visitor) {
        return visitor.visit(root);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public TreeSnapshot<T> snapshot() {
        return new TreeSnapshot<>(traverse(new InOrderTraversal<>(root)), getAnalyzer().getMetrics());
    }

    @Override
    public void restore(TreeSnapshot<T> snapshot) {
        clear();
        for (T value : snapshot.values) {
            insert(value);
        }
    }

    public OperationMetrics getOperationMetrics() {
        double avgSearchMs = searchCount > 0 ? (totalSearchTimeNs / (double) searchCount) / 1_000_000 : 0;
        double avgInsertMs = insertCount > 0 ? (totalInsertTimeNs / (double) insertCount) / 1_000_000 : 0;
        return new OperationMetrics(insertCount, deleteCount, searchCount, rangeQueryCount, avgSearchMs, avgInsertMs);
    }

    public List<TreeEvent<T>> getEventLog() {
        if (eventLog == null) return Collections.emptyList();
        return Collections.unmodifiableList(eventLog);
    }

    public CacheStatistics getCacheStatistics() {
        if (queryCache == null) throw new IllegalStateException("Caching is disabled");
        return queryCache.getStats();
    }

    public TreeStatistics<T> getTreeStatistics() {
        List<T> sorted = traverse(new InOrderTraversal<>(root));
        int leafCount = countLeaves(root);
        int internalCount = size - leafCount;
        double avgDepth = calculateAvgDepth(root);
        double avgBranching = size > 1 ? (double) (size - 1) / size : 0;

        return new TreeStatistics<>(size, leafCount, internalCount, avgDepth, avgBranching, sorted);
    }

    private int countLeaves(Node<T> node) {
        if (node == null) return 0;
        if (node.left == null && node.right == null) return 1;
        return countLeaves(node.left) + countLeaves(node.right);
    }

    private double calculateAvgDepth(Node<T> node) {
        if (node == null || size == 0) return 0;
        return (double) calculateDepthSum(node, 0) / size;
    }

    private int calculateDepthSum(Node<T> node, int depth) {
        if (node == null) return 0;
        return depth + calculateDepthSum(node.left, depth + 1) + calculateDepthSum(node.right, depth + 1);
    }

    public TreeDifference<T> compareTo(BinarySearchTree<T> other) {
        if (other == null) throw new IllegalArgumentException("Other tree cannot be null");

        List<T> thisList = traverse(new InOrderTraversal<>(root));
        List<T> otherList = other.traverse(new InOrderTraversal<>(other.root));

        Set<T> thisSet = new HashSet<>(thisList);
        Set<T> otherSet = new HashSet<>(otherList);

        List<T> onlyInFirst = thisList.stream()
            .filter(v -> !otherSet.contains(v))
            .collect(Collectors.toList());

        List<T> onlyInSecond = otherList.stream()
            .filter(v -> !thisSet.contains(v))
            .collect(Collectors.toList());

        List<T> common = thisList.stream()
            .filter(otherSet::contains)
            .collect(Collectors.toList());

        return new TreeDifference<>(onlyInFirst, onlyInSecond, common);
    }

    public BinarySearchTree<T> deepCopy() {
        BinarySearchTree<T> copy = new BinarySearchTree<>(config);
        if (root != null) {
            copy.root = root.deepCopy();
            copy.size = this.size;
        }
        return copy;
    }

    public void clearMetrics() {
        totalInsertTimeNs = 0;
        totalSearchTimeNs = 0;
        insertCount = 0;
        searchCount = 0;
        deleteCount = 0;
        rangeQueryCount = 0;
        cacheHits = 0;
        cacheMisses = 0;
        if (eventLog != null) eventLog.clear();
    }

    public void validateIntegrity() throws BSTIntegrityException {
        IntegrityCheckResult result = getValidator().performFullIntegrityCheck(root);
        if (!result.isValid) {
            String violations = String.join("; ", result.violations);
            throw new BSTIntegrityException("Tree integrity violated: " + violations);
        }
    }

    public IntegrityCheckResult checkIntegrity() {
        return getValidator().performFullIntegrityCheck(root);
    }

    public List<T> stream() {
        return traverse(new InOrderTraversal<>(root));
    }

    public List<T> filterBy(TreeFilter<T> filter) {
        return filter.apply(stream());
    }

    public <R> R mapTo(Function<List<T>, R> mapper) {
        return mapper.apply(stream());
    }

    public void forEachInOrder(Consumer<T> action) {
        forEachInOrderRecursive(root, action);
    }

    private void forEachInOrderRecursive(Node<T> node, Consumer<T> action) {
        if (node == null) return;
        forEachInOrderRecursive(node.left, action);
        action.accept(node.value);
        forEachInOrderRecursive(node.right, action);
    }

    public ChangeSet<T> getChangeSet() {
        return changeSet;
    }

    public TreePath<T> getPath(T value) {
        if (value == null) throw new BSTOperationException("Value cannot be null");
        List<T> path = new ArrayList<>();
        getPathRecursive(root, value, path);
        return new TreePath<>(path);
    }

    private boolean getPathRecursive(Node<T> node, T value, List<T> path) {
        if (node == null) return false;

        path.add(node.value);
        int comparison = value.compareTo(node.value);

        if (comparison == 0) return true;
        else if (comparison < 0) {
            if (getPathRecursive(node.left, value, path)) return true;
        } else {
            if (getPathRecursive(node.right, value, path)) return true;
        }

        path.remove(path.size() - 1);
        return false;
    }

    public LCAResult<T> findLCA(T value1, T value2) {
        if (value1 == null || value2 == null) throw new BSTOperationException("Values cannot be null");
        if (!search(value1) || !search(value2)) {
            List<T> emptyList = new ArrayList<>();
            return new LCAResult<>(Optional.empty(), new TreePath<>(emptyList),
                                 new TreePath<>(emptyList), 0);
        }

        List<T> path1 = new ArrayList<>();
        List<T> path2 = new ArrayList<>();
        getPathRecursive(root, value1, path1);
        getPathRecursive(root, value2, path2);

        T lca = null;
        int lcaIndex = 0;
        for (int i = 0; i < Math.min(path1.size(), path2.size()); i++) {
            if (path1.get(i).equals(path2.get(i))) {
                lca = path1.get(i);
                lcaIndex = i;
            } else {
                break;
            }
        }

        List<T> pathToLca1 = new ArrayList<>(path1.subList(lcaIndex, path1.size()));
        List<T> pathToLca2 = new ArrayList<>(path2.subList(lcaIndex, path2.size()));

        int distance = pathToLca1.size() + pathToLca2.size() - 2;

        return new LCAResult<>(Optional.ofNullable(lca), new TreePath<>(pathToLca1),
                             new TreePath<>(pathToLca2), distance);
    }

    public SubtreeInfo<T> getSubtreeInfo(T value) {
        if (value == null) throw new BSTOperationException("Value cannot be null");
        Node<T> subtreeRoot = findNode(root, value);
        if (subtreeRoot == null) {
            throw new BSTOperationException("Value not found in tree: " + value);
        }

        int subtreeSize = getSubtreeSize(subtreeRoot);
        int subtreeHeight = getSubtreeHeight(subtreeRoot);
        List<T> subtreeValues = new ArrayList<>();
        traverseSubtreeInOrder(subtreeRoot, subtreeValues);

        return new SubtreeInfo<>(value, subtreeSize, subtreeHeight, subtreeValues);
    }

    private Node<T> findNode(Node<T> node, T value) {
        if (node == null) return null;
        int comparison = value.compareTo(node.value);
        if (comparison == 0) return node;
        else if (comparison < 0) return findNode(node.left, value);
        else return findNode(node.right, value);
    }

    private int getSubtreeSize(Node<T> node) {
        if (node == null) return 0;
        return 1 + getSubtreeSize(node.left) + getSubtreeSize(node.right);
    }

    private int getSubtreeHeight(Node<T> node) {
        if (node == null) return -1;
        return 1 + Math.max(getSubtreeHeight(node.left), getSubtreeHeight(node.right));
    }

    private void traverseSubtreeInOrder(Node<T> node, List<T> result) {
        if (node == null) return;
        traverseSubtreeInOrder(node.left, result);
        result.add(node.value);
        traverseSubtreeInOrder(node.right, result);
    }

    public String visualize() {
        TreeVisualization viz = new TreeVisualization();
        return viz.visualize(root);
    }

    public MemoryEstimate estimateMemory() {
        int nodeCount = this.size;
        long bytesPerNode = 64;
        return new MemoryEstimate(nodeCount, bytesPerNode);
    }

    public void printVisualization() {
        System.out.println(visualize());
    }

    public TreeQuery<T> query() {
        return new TreeQuery<>(this);
    }

    public String exportToJSON() {
        return TreeExport.toJSON(this);
    }

    public String exportToDOT() {
        return TreeExport.toDOT(this);
    }

    public String exportToCSV() {
        return TreeExport.toCSV(this);
    }

    public TreeTransaction<T> beginTransaction() {
        return new TreeTransaction<>(this);
    }

    public PerformanceReport getPerformanceReport() {
        return profiler.getReport();
    }

    public void clearPerformanceData() {
        profiler.clear();
    }

    public String getConfigurationInfo() {
        return String.format("TreeConfig{logging=%s, metrics=%s, caching=%s, cacheSize=%d, threadSafe=%s}",
                           config.enableEventLogging, config.enableMetrics, config.enableCaching,
                           config.cacheSize, config.threadSafe);
    }

    @Override
    public String toString() {
        return "BST(" + traverse(new InOrderTraversal<>(root)) + ")";
    }

    public static class BSTBuilder<T extends Comparable<T>> {
        private BinarySearchTree<T> tree = new BinarySearchTree<>();

        public BSTBuilder<T> add(T value) {
            tree.insert(value);
            return this;
        }

        @SafeVarargs
        public final BSTBuilder<T> addAll(T... values) {
            for (T value : values) {
                tree.insert(value);
            }
            return this;
        }

        public BinarySearchTree<T> build() {
            return tree;
        }
    }
}

class LoggingListener<T> implements TreeEventListener<T> {
    @Override
    public void onInsert(T value) {
        System.out.println("  [Event] Insert: " + value);
    }

    @Override
    public void onDelete(T value) {
        System.out.println("  [Event] Delete: " + value);
    }

    @Override
    public void onClear() {
        System.out.println("  [Event] Clear all");
    }
}

class BinarySearchTreeDemo {
    public static void main(String[] args) {
        System.out.println("=== Building Tree ===");
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        for (int val : new int[]{6, 2, 8, 7, 9, 1, 4, 3, 5}) {
            bst.insert(val);
        }
        System.out.println("Tree: " + bst);

        System.out.println("\n=== Query Builder ===");
        List<Integer> gtFive = bst.query().filter(v -> v > 5).execute();
        System.out.println("Values > 5: " + gtFive);

        List<Integer> rangeResult = bst.query().inRange(3, 7).execute();
        System.out.println("Range [3,7]: " + rangeResult);

        int count = bst.query().filter(v -> v % 2 == 0).count();
        System.out.println("Even numbers count: " + count);

        Optional<Integer> first = bst.query().filter(v -> v > 4).first();
        System.out.println("First value > 4: " + first);

        System.out.println("\n=== Export Formats ===");
        System.out.println("JSON Export:");
        System.out.println(bst.exportToJSON());

        System.out.println("\nDOT Export (first 5 lines):");
        String[] dotLines = bst.exportToDOT().split("\n");
        for (int i = 0; i < Math.min(5, dotLines.length); i++) {
            System.out.println(dotLines[i]);
        }

        System.out.println("\nCSV Export (first 3 lines):");
        String[] csvLines = bst.exportToCSV().split("\n");
        for (int i = 0; i < Math.min(3, csvLines.length); i++) {
            System.out.println(csvLines[i]);
        }

        System.out.println("\n=== Transaction Support ===");
        System.out.println("Before transaction: " + bst);
        TreeTransaction<Integer> txn = bst.beginTransaction();
        System.out.println(txn);

        txn.execute(tree -> {
            tree.delete(1);
            tree.delete(9);
        });
        System.out.println("After delete: " + bst);

        txn.commit();
        System.out.println("Committed: " + txn);

        TreeTransaction<Integer> txn2 = bst.beginTransaction();
        txn2.execute(tree -> tree.insert(1));
        System.out.println("Inserted 1: " + bst);
        txn2.rollback();
        System.out.println("After rollback: " + bst);

        System.out.println("\n=== Path Operations ===");
        TreePath<Integer> path = bst.getPath(5);
        System.out.println("Path to 5: " + path);

        System.out.println("\n=== LCA Queries ===");
        LCAResult<Integer> lca = bst.findLCA(3, 8);
        System.out.println("LCA(3, 8): " + lca);

        System.out.println("\n=== Subtree Analysis ===");
        SubtreeInfo<Integer> subtree = bst.getSubtreeInfo(2);
        System.out.println("Subtree at 2: " + subtree);

        System.out.println("\n=== Tree Statistics ===");
        System.out.println(bst.getTreeStatistics());
        System.out.println(bst.getAnalyzer().getMetrics());

        System.out.println("\n=== Memory Estimation ===");
        System.out.println(bst.estimateMemory());

        System.out.println("\n=== Batch Operations ===");
        BatchOperationResult<Integer> batch = bst.insertBatch(10, 11, 12);
        System.out.println("Batch result: " + batch);
        System.out.println("After batch: " + bst);

        System.out.println("\n=== Deep Copy & Comparison ===");
        BinarySearchTree<Integer> copy = bst.deepCopy();
        copy.delete(5);
        TreeDifference<Integer> diff = bst.compareTo(copy);
        System.out.println("Difference: " + diff);

        System.out.println("\n=== Integrity & Validation ===");
        System.out.println(bst.checkIntegrity());

        System.out.println("\n=== Performance Report ===");
        PerformanceReport report = bst.getPerformanceReport();
        System.out.println(report);

        System.out.println("\n=== Change Tracking ===");
        System.out.println(bst.getChangeSet());

        System.out.println("\n=== String BST Example ===");
        BinarySearchTree<String> words = new BinarySearchTree<>();
        for (String word : new String[]{"dog", "cat", "zebra"}) {
            words.insert(word);
        }
        System.out.println("Words: " + words);
        System.out.println("Query builder: " + words.query().filter(w -> w.startsWith("c")).execute());

        System.out.println("\n=== Exception Handling ===");
        try {
            bst.insert(null);
        } catch (BSTOperationException e) {
            System.out.println("Caught: " + e.getClass().getSimpleName());
        }
    }
}

interface Function<T, R> {
    R apply(T input);
}

interface Consumer<T> {
    void accept(T value);
}
