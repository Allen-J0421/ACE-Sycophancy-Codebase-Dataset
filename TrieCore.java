import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Exception classes
class TrieException extends RuntimeException {
    TrieException(String message) {
        super(message);
    }

    TrieException(String message, Throwable cause) {
        super(message, cause);
    }
}

class InvalidWordException extends TrieException {
    InvalidWordException(String message) {
        super(message);
    }
}

// Result wrapper for operations
class OperationResult<T> {
    private final T value;
    private final boolean success;
    private final String message;
    private final long executionTimeMs;

    private OperationResult(T value, boolean success, String message, long executionTimeMs) {
        this.value = value;
        this.success = success;
        this.message = message;
        this.executionTimeMs = executionTimeMs;
    }

    static <T> OperationResult<T> success(T value) {
        return success(value, 0);
    }

    static <T> OperationResult<T> success(T value, long executionTimeMs) {
        return new OperationResult<>(value, true, "Success", executionTimeMs);
    }

    static <T> OperationResult<T> failure(String message) {
        return failure(message, 0);
    }

    static <T> OperationResult<T> failure(String message, long executionTimeMs) {
        return new OperationResult<>(null, false, message, executionTimeMs);
    }

    boolean isSuccess() {
        return success;
    }

    T getValue() {
        return value;
    }

    String getMessage() {
        return message;
    }

    long getExecutionTimeMs() {
        return executionTimeMs;
    }
}

// Core interfaces
interface CharacterSet {
    int toIndex(char c);
    char fromIndex(int index);
    int getSize();
    boolean isValid(char c);
}

@FunctionalInterface
interface TrieOperation {
    void execute();
}

interface TrieOperationListener {
    void onInsert(String word);
    void onDelete(String word);
    void onSearch(String word, boolean found);
}

interface WordPredicate {
    boolean test(String word, TrieNode node);
}

interface TrieVisitor {
    void visit(String word, TrieNode node, int depth);
}

interface CachePolicy<K, V> {
    V get(K key);
    void put(K key, V value);
    void invalidate(K key);
    void clear();
    int size();
}

interface TrieMetrics {
    void recordInsert();
    void recordSearch(boolean found);
    void recordDelete();
    long getTotalInserts();
    long getTotalSearches();
    long getTotalSearchHits();
    long getTotalDeletes();
    double getHitRate();
}

// Trie node state enumeration
enum TrieNodeState {
    INTERMEDIATE("Not a word endpoint"),
    TERMINAL("Word endpoint"),
    DEPRECATED("Marked for deletion");

    private final String description;

    TrieNodeState(String description) {
        this.description = description;
    }

    String getDescription() {
        return description;
    }
}

// Core node structure
class TrieNode {
    private final TrieNode[] children;
    private TrieNodeState state;
    private int frequency;
    private long timestamp;

    TrieNode(int charsetSize) {
        this.children = new TrieNode[charsetSize];
        this.state = TrieNodeState.INTERMEDIATE;
        this.frequency = 0;
        this.timestamp = System.currentTimeMillis();
    }

    TrieNode getChild(int index) {
        return children[index];
    }

    void setChild(int index, TrieNode node) {
        children[index] = node;
    }

    void markAsWord() {
        this.state = TrieNodeState.TERMINAL;
        this.frequency++;
        this.timestamp = System.currentTimeMillis();
    }

    void unmarkAsWord() {
        this.state = TrieNodeState.INTERMEDIATE;
        this.frequency = Math.max(0, frequency - 1);
        this.timestamp = System.currentTimeMillis();
    }

    boolean isTerminal() {
        return state == TrieNodeState.TERMINAL;
    }

    TrieNodeState getState() {
        return state;
    }

    int getFrequency() {
        return frequency;
    }

    long getTimestamp() {
        return timestamp;
    }

    boolean hasChild(int index) {
        return children[index] != null;
    }

    int childCount() {
        int count = 0;
        for (TrieNode child : children) {
            if (child != null) count++;
        }
        return count;
    }

    boolean isLeaf() {
        return childCount() == 0;
    }
}
