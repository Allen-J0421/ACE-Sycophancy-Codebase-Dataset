import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.time.Instant;
import java.util.UUID;

// Trie builder for fluent configuration
class TrieBuilder {
    private CharacterSet charset = new LowercaseCharacterSet();
    private int maxWordLength = Integer.MAX_VALUE;
    private boolean enableCaching = true;
    private int cacheSize = 1024;
    private boolean enableMetrics = true;

    TrieBuilder withCharacterSet(CharacterSet charset) {
        this.charset = charset;
        return this;
    }

    TrieBuilder withMaxWordLength(int maxLength) {
        this.maxWordLength = maxLength;
        return this;
    }

    TrieBuilder withCaching(boolean enable) {
        this.enableCaching = enable;
        return this;
    }

    TrieBuilder withCacheSize(int size) {
        this.cacheSize = size;
        return this;
    }

    TrieBuilder withMetrics(boolean enable) {
        this.enableMetrics = enable;
        return this;
    }

    Trie build() {
        CachePolicy<String, TrieNode> cache = enableCaching ? new LRUCache<>(cacheSize) : new NoOpCache<>();
        TrieMetrics metrics = enableMetrics ? new TrieMetricsCollector() : new NoOpMetrics();
        return new Trie(charset, maxWordLength, cache, metrics);
    }
}

// Main Trie implementation with state machine
class Trie {
    private final TrieNode root;
    private final CharacterSet charset;
    private final InputValidator validator;
    private final List<TrieOperationListener> listeners;
    private final Stack<TrieOperation> operationHistory;
    private final TrieTraversalEngine traversalEngine;
    private final ReadWriteLock lock;
    private TrieState state;
    private int size;

    public Trie() {
        this(new LowercaseCharacterSet());
    }

    public Trie(CharacterSet charset) {
        this(charset, Integer.MAX_VALUE, new LRUCache<>(1024), new TrieMetricsCollector());
    }

    public Trie(CharacterSet charset, int maxWordLength, CachePolicy<String, TrieNode> cache, TrieMetrics metrics) {
        this.charset = charset;
        this.root = new TrieNode(charset.getSize());
        this.validator = new InputValidator(charset, maxWordLength);
        this.listeners = new CopyOnWriteArrayList<>();
        this.operationHistory = new Stack<>();
        this.traversalEngine = new TrieTraversalEngine(charset, cache, metrics);
        this.lock = new ReentrantReadWriteLock();
        this.state = TrieState.INITIALIZED;
        this.size = 0;
    }

    public void addListener(TrieOperationListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeListener(TrieOperationListener listener) {
        listeners.remove(listener);
    }

    public OperationResult<Void> insert(String word) {
        lock.writeLock().lock();
        long startTime = System.currentTimeMillis();
        try {
            if (state != TrieState.INITIALIZED) {
                return OperationResult.failure("Trie is " + state.getDescription(), System.currentTimeMillis() - startTime);
            }

            OperationResult<Void> validation = validator.validate(word);
            if (!validation.isSuccess()) {
                return validation;
            }

            if (!searchInternal(word)) {
                TrieNode node = traversalEngine.traverseOrCreate(root, word);
                node.markAsWord();
                size++;
                traversalEngine.getMetrics().recordInsert();
                notifyInsert(word);
                operationHistory.push(() -> node.unmarkAsWord());
            }
            return OperationResult.success(null, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            return OperationResult.failure("Insert failed: " + e.getMessage(), System.currentTimeMillis() - startTime);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean search(String word) {
        if (!validator.isValid(word)) {
            return false;
        }
        lock.readLock().lock();
        try {
            return searchInternal(word);
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean searchInternal(String word) {
        TrieNode node = traversalEngine.traverseReadOnly(root, word);
        boolean found = node != null && node.isTerminal();
        traversalEngine.getMetrics().recordSearch(found);
        notifySearch(word, found);
        return found;
    }

    public boolean isPrefix(String prefix) {
        if (!validator.isValid(prefix)) {
            return false;
        }
        lock.readLock().lock();
        try {
            return traversalEngine.traverseReadOnly(root, prefix) != null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getSize() {
        lock.readLock().lock();
        try {
            return size;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<String> getAllWords() {
        lock.readLock().lock();
        try {
            return collectWordsByPredicate(root, (w, n) -> true, new StringBuilder());
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<String> findWordsWithPrefix(String prefix) {
        if (!validator.isValid(prefix)) {
            return Collections.emptyList();
        }

        lock.readLock().lock();
        try {
            TrieNode node = traversalEngine.traverseReadOnly(root, prefix);
            if (node == null) {
                return Collections.emptyList();
            }
            return collectWordsByPredicate(node, (w, n) -> true, new StringBuilder(prefix));
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<String> findWordsByFrequency(int minFrequency) {
        lock.readLock().lock();
        try {
            return collectWordsByPredicate(root,
                (w, n) -> n.getFrequency() >= minFrequency,
                new StringBuilder());
        } finally {
            lock.readLock().unlock();
        }
    }

    public OperationResult<Boolean> delete(String word) {
        lock.writeLock().lock();
        long startTime = System.currentTimeMillis();
        try {
            if (state != TrieState.INITIALIZED) {
                return OperationResult.failure("Trie is " + state.getDescription(), System.currentTimeMillis() - startTime);
            }

            OperationResult<Void> validation = validator.validate(word);
            if (!validation.isSuccess()) {
                return OperationResult.failure(validation.getMessage(), System.currentTimeMillis() - startTime);
            }

            if (!searchInternal(word)) {
                return OperationResult.failure("Word not found", System.currentTimeMillis() - startTime);
            }

            TrieNode node = traversalEngine.traverseReadOnly(root, word);
            if (node != null) {
                node.unmarkAsWord();
                size--;
                traversalEngine.getMetrics().recordDelete();
                notifyDelete(word);
                traversalEngine.clearCache();
                return OperationResult.success(true, System.currentTimeMillis() - startTime);
            }
            return OperationResult.failure("Deletion failed", System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            return OperationResult.failure("Delete failed: " + e.getMessage(), System.currentTimeMillis() - startTime);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public TrieStatistics getStatistics() {
        lock.readLock().lock();
        try {
            int[] stats = computeStats(root, 0);
            return new TrieStatistics(size, stats[0], stats[1]);
        } finally {
            lock.readLock().unlock();
        }
    }

    public TrieMetrics getMetrics() {
        return traversalEngine.getMetrics();
    }

    public void forEach(Consumer<String> action) {
        getAllWords().forEach(action);
    }

    public void accept(TrieVisitor visitor) {
        lock.readLock().lock();
        try {
            acceptVisitor(root, visitor, "", 0);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setReadOnly(boolean readOnly) {
        lock.writeLock().lock();
        try {
            this.state = readOnly ? TrieState.READ_ONLY : TrieState.INITIALIZED;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isReadOnly() {
        lock.readLock().lock();
        try {
            return state == TrieState.READ_ONLY;
        } finally {
            lock.readLock().unlock();
        }
    }

    public TrieState getState() {
        lock.readLock().lock();
        try {
            return state;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void freeze() {
        lock.writeLock().lock();
        try {
            this.state = TrieState.FROZEN;
            traversalEngine.clearCache();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            if (state == TrieState.FROZEN) {
                throw new TrieException("Cannot clear frozen trie");
            }
            for (int i = 0; i < charset.getSize(); i++) {
                root.setChild(i, null);
            }
            size = 0;
            traversalEngine.clearCache();
            listeners.forEach(l -> l.onDelete("*"));
        } finally {
            lock.writeLock().unlock();
        }
    }

    private List<String> collectWordsByPredicate(TrieNode node, WordPredicate predicate, StringBuilder current) {
        List<String> words = new ArrayList<>();
        dfsPredicate(node, current, words, predicate);
        return words;
    }

    private int[] computeStats(TrieNode node, int depth) {
        int nodeCount = 1;
        int maxDepth = depth;

        for (int i = 0; i < charset.getSize(); i++) {
            if (node.hasChild(i)) {
                int[] childStats = computeStats(node.getChild(i), depth + 1);
                nodeCount += childStats[0];
                maxDepth = Math.max(maxDepth, childStats[1]);
            }
        }

        return new int[]{nodeCount, maxDepth};
    }

    private void dfsPredicate(TrieNode node, StringBuilder current, List<String> words, WordPredicate predicate) {
        if (node.isTerminal() && predicate.test(current.toString(), node)) {
            words.add(current.toString());
        }

        for (int i = 0; i < charset.getSize(); i++) {
            if (node.hasChild(i)) {
                current.append(charset.fromIndex(i));
                dfsPredicate(node.getChild(i), current, words, predicate);
                current.deleteCharAt(current.length() - 1);
            }
        }
    }

    private void acceptVisitor(TrieNode node, TrieVisitor visitor, String current, int depth) {
        if (node.isTerminal()) {
            visitor.visit(current, node, depth);
        }

        for (int i = 0; i < charset.getSize(); i++) {
            if (node.hasChild(i)) {
                acceptVisitor(node.getChild(i), visitor, current + charset.fromIndex(i), depth + 1);
            }
        }
    }

    private void notifyInsert(String word) {
        listeners.forEach(l -> l.onInsert(word));
    }

    private void notifyDelete(String word) {
        listeners.forEach(l -> l.onDelete(word));
    }

    private void notifySearch(String word, boolean found) {
        listeners.forEach(l -> l.onSearch(word, found));
    }
}
