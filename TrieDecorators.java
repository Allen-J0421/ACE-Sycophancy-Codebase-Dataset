import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Decorator pattern for Trie enhancement
abstract class TrieDecorator extends Trie {
    protected final Trie delegate;

    TrieDecorator(Trie delegate) {
        super(new LowercaseCharacterSet());
        this.delegate = delegate;
    }

    @Override
    public OperationResult<Void> insert(String word) {
        return delegate.insert(word);
    }

    @Override
    public boolean search(String word) {
        return delegate.search(word);
    }

    @Override
    public boolean isPrefix(String prefix) {
        return delegate.isPrefix(prefix);
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public List<String> getAllWords() {
        return delegate.getAllWords();
    }

    @Override
    public List<String> findWordsWithPrefix(String prefix) {
        return delegate.findWordsWithPrefix(prefix);
    }

    @Override
    public OperationResult<Boolean> delete(String word) {
        return delegate.delete(word);
    }

    @Override
    public TrieStatistics getStatistics() {
        return delegate.getStatistics();
    }

    @Override
    public TrieMetrics getMetrics() {
        return delegate.getMetrics();
    }
}

// Decorator: Caching wrapper
class CachedTrieDecorator extends TrieDecorator {
    private final Map<String, Boolean> searchCache;
    private final Map<String, List<String>> prefixCache;
    private final int cacheSize;

    CachedTrieDecorator(Trie delegate, int cacheSize) {
        super(delegate);
        this.cacheSize = cacheSize;
        this.searchCache = new LinkedHashMap<String, Boolean>(cacheSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > cacheSize;
            }
        };
        this.prefixCache = new LinkedHashMap<String, List<String>>(cacheSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > cacheSize;
            }
        };
    }

    @Override
    public boolean search(String word) {
        if (searchCache.containsKey(word)) {
            return searchCache.get(word);
        }
        boolean result = delegate.search(word);
        searchCache.put(word, result);
        return result;
    }

    @Override
    public List<String> findWordsWithPrefix(String prefix) {
        if (prefixCache.containsKey(prefix)) {
            return new ArrayList<>(prefixCache.get(prefix));
        }
        List<String> results = delegate.findWordsWithPrefix(prefix);
        prefixCache.put(prefix, results);
        return results;
    }

    @Override
    public OperationResult<Void> insert(String word) {
        searchCache.clear();
        prefixCache.clear();
        return delegate.insert(word);
    }

    @Override
    public OperationResult<Boolean> delete(String word) {
        searchCache.clear();
        prefixCache.clear();
        return delegate.delete(word);
    }

    int getCacheSize() {
        return searchCache.size() + prefixCache.size();
    }
}

// Decorator: Logging wrapper
class LoggingTrieDecorator extends TrieDecorator {
    private final String name;

    LoggingTrieDecorator(Trie delegate, String name) {
        super(delegate);
        this.name = name;
    }

    @Override
    public OperationResult<Void> insert(String word) {
        System.out.println("[" + name + "] INSERT: " + word);
        OperationResult<Void> result = delegate.insert(word);
        System.out.println("[" + name + "] INSERT result: " + result.getMessage());
        return result;
    }

    @Override
    public boolean search(String word) {
        System.out.println("[" + name + "] SEARCH: " + word);
        boolean result = delegate.search(word);
        System.out.println("[" + name + "] SEARCH result: " + result);
        return result;
    }

    @Override
    public OperationResult<Boolean> delete(String word) {
        System.out.println("[" + name + "] DELETE: " + word);
        OperationResult<Boolean> result = delegate.delete(word);
        System.out.println("[" + name + "] DELETE result: " + result.getMessage());
        return result;
    }
}

// Decorator: Statistics tracking
class StatisticsTrieDecorator extends TrieDecorator {
    private int insertCount;
    private int deleteCount;
    private int searchCount;
    private int searchHits;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    StatisticsTrieDecorator(Trie delegate) {
        super(delegate);
    }

    @Override
    public OperationResult<Void> insert(String word) {
        lock.writeLock().lock();
        try {
            insertCount++;
            return delegate.insert(word);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean search(String word) {
        lock.writeLock().lock();
        try {
            searchCount++;
            boolean result = delegate.search(word);
            if (result) searchHits++;
            return result;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public OperationResult<Boolean> delete(String word) {
        lock.writeLock().lock();
        try {
            deleteCount++;
            return delegate.delete(word);
        } finally {
            lock.writeLock().unlock();
        }
    }

    Map<String, Integer> getStats() {
        lock.readLock().lock();
        try {
            Map<String, Integer> stats = new LinkedHashMap<>();
            stats.put("inserts", insertCount);
            stats.put("deletes", deleteCount);
            stats.put("searches", searchCount);
            stats.put("hits", searchHits);
            stats.put("hitRate", searchCount == 0 ? 0 : (searchHits * 100) / searchCount);
            return stats;
        } finally {
            lock.readLock().unlock();
        }
    }
}

// Decorator factory
class TrieDecoratorFactory {
    static CachedTrieDecorator withCaching(Trie trie, int cacheSize) {
        return new CachedTrieDecorator(trie, cacheSize);
    }

    static LoggingTrieDecorator withLogging(Trie trie, String name) {
        return new LoggingTrieDecorator(trie, name);
    }

    static StatisticsTrieDecorator withStatistics(Trie trie) {
        return new StatisticsTrieDecorator(trie);
    }

    static Trie withAll(Trie trie, String name) {
        Trie logged = withLogging(trie, name);
        Trie cached = withCaching(logged, 256);
        return withStatistics(cached);
    }
}
