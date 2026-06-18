import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Character set implementations
class LowercaseCharacterSet implements CharacterSet {
    private static final int SIZE = 26;
    private static final int OFFSET = 'a';

    @Override
    public int toIndex(char c) {
        return c - OFFSET;
    }

    @Override
    public char fromIndex(int index) {
        return (char) (index + OFFSET);
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public boolean isValid(char c) {
        int index = toIndex(c);
        return index >= 0 && index < SIZE;
    }
}

// Input validation
class InputValidator {
    private final CharacterSet charset;
    private final int maxWordLength;

    InputValidator(CharacterSet charset) {
        this(charset, Integer.MAX_VALUE);
    }

    InputValidator(CharacterSet charset, int maxWordLength) {
        this.charset = charset;
        this.maxWordLength = maxWordLength;
    }

    boolean isValid(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        if (input.length() > maxWordLength) {
            return false;
        }
        for (char c : input.toCharArray()) {
            if (!charset.isValid(c)) {
                return false;
            }
        }
        return true;
    }

    OperationResult<Void> validate(String input) {
        if (input == null) {
            return OperationResult.failure("Input cannot be null");
        }
        if (input.isEmpty()) {
            return OperationResult.failure("Input cannot be empty");
        }
        if (input.length() > maxWordLength) {
            return OperationResult.failure("Input exceeds maximum length: " + maxWordLength);
        }
        for (char c : input.toCharArray()) {
            if (!charset.isValid(c)) {
                return OperationResult.failure("Invalid character: " + c);
            }
        }
        return OperationResult.success(null);
    }
}

// LRU Cache
class LRUCache<K, V> implements CachePolicy<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> cache;

    LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<K, V>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > capacity;
            }
        };
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public void invalidate(K key) {
        cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int size() {
        return cache.size();
    }
}

// Metrics collection
class TrieMetricsCollector implements TrieMetrics {
    private long totalInserts;
    private long totalSearches;
    private long totalSearchHits;
    private long totalDeletes;

    @Override
    public void recordInsert() {
        totalInserts++;
    }

    @Override
    public void recordSearch(boolean found) {
        totalSearches++;
        if (found) totalSearchHits++;
    }

    @Override
    public void recordDelete() {
        totalDeletes++;
    }

    @Override
    public long getTotalInserts() {
        return totalInserts;
    }

    @Override
    public long getTotalSearches() {
        return totalSearches;
    }

    @Override
    public long getTotalSearchHits() {
        return totalSearchHits;
    }

    @Override
    public long getTotalDeletes() {
        return totalDeletes;
    }

    @Override
    public double getHitRate() {
        return totalSearches == 0 ? 0.0 : (double) totalSearchHits / totalSearches;
    }

    @Override
    public String toString() {
        return String.format("TrieMetrics{inserts=%d, searches=%d, hits=%d, deletes=%d, hitRate=%.2f%%}",
            totalInserts, totalSearches, totalSearchHits, totalDeletes, getHitRate() * 100);
    }
}

// No-op implementations
class NoOpCache<K, V> implements CachePolicy<K, V> {
    @Override
    public V get(K key) { return null; }
    @Override
    public void put(K key, V value) { }
    @Override
    public void invalidate(K key) { }
    @Override
    public void clear() { }
    @Override
    public int size() { return 0; }
}

class NoOpMetrics implements TrieMetrics {
    @Override
    public void recordInsert() { }
    @Override
    public void recordSearch(boolean found) { }
    @Override
    public void recordDelete() { }
    @Override
    public long getTotalInserts() { return 0; }
    @Override
    public long getTotalSearches() { return 0; }
    @Override
    public long getTotalSearchHits() { return 0; }
    @Override
    public long getTotalDeletes() { return 0; }
    @Override
    public double getHitRate() { return 0.0; }
}

// Statistics
class TrieStatistics {
    private final int totalWords;
    private final int totalNodes;
    private final int maxDepth;
    private final long timestamp;

    TrieStatistics(int totalWords, int totalNodes, int maxDepth) {
        this.totalWords = totalWords;
        this.totalNodes = totalNodes;
        this.maxDepth = maxDepth;
        this.timestamp = System.currentTimeMillis();
    }

    public int getTotalWords() {
        return totalWords;
    }

    public int getTotalNodes() {
        return totalNodes;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("TrieStatistics{words=%d, nodes=%d, maxDepth=%d, timestamp=%d}",
            totalWords, totalNodes, maxDepth, timestamp);
    }
}

// Traversal strategies
abstract class TrieTraversalStrategy {
    protected final CharacterSet charset;

    TrieTraversalStrategy(CharacterSet charset) {
        this.charset = charset;
    }

    abstract TrieNode traverse(TrieNode root, String input);
}

class ReadOnlyTraversalStrategy extends TrieTraversalStrategy {
    ReadOnlyTraversalStrategy(CharacterSet charset) {
        super(charset);
    }

    @Override
    TrieNode traverse(TrieNode root, String input) {
        TrieNode curr = root;
        for (char c : input.toCharArray()) {
            int index = charset.toIndex(c);
            if (!curr.hasChild(index)) {
                return null;
            }
            curr = curr.getChild(index);
        }
        return curr;
    }
}

class CreatingTraversalStrategy extends TrieTraversalStrategy {
    CreatingTraversalStrategy(CharacterSet charset) {
        super(charset);
    }

    @Override
    TrieNode traverse(TrieNode root, String input) {
        TrieNode curr = root;
        for (char c : input.toCharArray()) {
            int index = charset.toIndex(c);
            if (!curr.hasChild(index)) {
                curr.setChild(index, new TrieNode(charset.getSize()));
            }
            curr = curr.getChild(index);
        }
        return curr;
    }
}

// Trie state machine
enum TrieState {
    INITIALIZED("Ready for operations"),
    READ_ONLY("In read-only mode"),
    FROZEN("Frozen, no operations allowed");

    private final String description;

    TrieState(String description) {
        this.description = description;
    }

    String getDescription() {
        return description;
    }
}

// Traversal engine with caching
class TrieTraversalEngine {
    private final CharacterSet charset;
    private final CachePolicy<String, TrieNode> searchCache;
    private final TrieMetrics metrics;

    TrieTraversalEngine(CharacterSet charset) {
        this(charset, new LRUCache<>(1024), new TrieMetricsCollector());
    }

    TrieTraversalEngine(CharacterSet charset, CachePolicy<String, TrieNode> cache, TrieMetrics metrics) {
        this.charset = charset;
        this.searchCache = cache;
        this.metrics = metrics;
    }

    TrieNode traverse(TrieNode root, String input, TrieTraversalStrategy strategy) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return strategy.traverse(root, input);
    }

    TrieNode traverseOrCreate(TrieNode root, String input) {
        searchCache.invalidate(input);
        return traverse(root, input, new CreatingTraversalStrategy(charset));
    }

    TrieNode traverseReadOnly(TrieNode root, String input) {
        TrieNode cached = searchCache.get(input);
        if (cached != null) {
            return cached;
        }
        TrieNode result = traverse(root, input, new ReadOnlyTraversalStrategy(charset));
        if (result != null) {
            searchCache.put(input, result);
        }
        return result;
    }

    TrieMetrics getMetrics() {
        return metrics;
    }

    void clearCache() {
        searchCache.clear();
    }
}
