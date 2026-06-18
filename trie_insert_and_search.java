import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private OperationResult(T value, boolean success, String message) {
        this.value = value;
        this.success = success;
        this.message = message;
    }

    static <T> OperationResult<T> success(T value) {
        return new OperationResult<>(value, true, "Success");
    }

    static <T> OperationResult<T> failure(String message) {
        return new OperationResult<>(null, false, message);
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
}

// Core interfaces and abstract classes
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

// Concrete character set implementation
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

// Core node structure
class TrieNode {
    private final TrieNode[] children;
    private boolean isWord;
    private int frequency;
    private long timestamp;

    TrieNode(int charsetSize) {
        this.children = new TrieNode[charsetSize];
        this.isWord = false;
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
        this.isWord = true;
        this.frequency++;
        this.timestamp = System.currentTimeMillis();
    }

    void unmarkAsWord() {
        this.isWord = false;
        this.frequency = Math.max(0, frequency - 1);
        this.timestamp = System.currentTimeMillis();
    }

    boolean isTerminal() {
        return isWord;
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

// Validation and input handling
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

// Statistics and metrics
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

// Navigation context for traversal
class NavigationContext {
    private final String input;
    private final CharacterSet charset;
    private int position;

    NavigationContext(String input, CharacterSet charset) {
        this.input = input;
        this.charset = charset;
        this.position = 0;
    }

    int nextIndex() {
        if (position < input.length()) {
            return charset.toIndex(input.charAt(position++));
        }
        return -1;
    }

    boolean hasNext() {
        return position < input.length();
    }

    int getPosition() {
        return position;
    }

    void reset() {
        position = 0;
    }
}

// Traversal strategy pattern
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

// Core traversal engine
class TrieTraversalEngine {
    private final CharacterSet charset;

    TrieTraversalEngine(CharacterSet charset) {
        this.charset = charset;
    }

    TrieNode traverse(TrieNode root, String input, TrieTraversalStrategy strategy) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return strategy.traverse(root, input);
    }

    TrieNode traverseOrCreate(TrieNode root, String input) {
        return traverse(root, input, new CreatingTraversalStrategy(charset));
    }

    TrieNode traverseReadOnly(TrieNode root, String input) {
        return traverse(root, input, new ReadOnlyTraversalStrategy(charset));
    }
}

// Main Trie implementation with builder pattern
class Trie {
    private final TrieNode root;
    private final CharacterSet charset;
    private final InputValidator validator;
    private final List<TrieOperationListener> listeners;
    private final Stack<TrieOperation> operationHistory;
    private final TrieTraversalEngine traversalEngine;
    private int size;
    private boolean isReadOnly;

    public Trie() {
        this(new LowercaseCharacterSet());
    }

    public Trie(CharacterSet charset) {
        this.charset = charset;
        this.root = new TrieNode(charset.getSize());
        this.validator = new InputValidator(charset);
        this.listeners = new CopyOnWriteArrayList<>();
        this.operationHistory = new Stack<>();
        this.traversalEngine = new TrieTraversalEngine(charset);
        this.size = 0;
        this.isReadOnly = false;
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
        if (isReadOnly) {
            return OperationResult.failure("Trie is in read-only mode");
        }

        OperationResult<Void> validation = validator.validate(word);
        if (!validation.isSuccess()) {
            return validation;
        }

        try {
            if (!searchInternal(word)) {
                TrieNode node = traversalEngine.traverseOrCreate(root, word);
                node.markAsWord();
                size++;
                notifyInsert(word);
                operationHistory.push(() -> node.unmarkAsWord());
            }
            return OperationResult.success(null);
        } catch (Exception e) {
            return OperationResult.failure("Insert failed: " + e.getMessage());
        }
    }

    public boolean search(String word) {
        if (!validator.isValid(word)) {
            return false;
        }
        return searchInternal(word);
    }

    private boolean searchInternal(String word) {
        TrieNode node = traversalEngine.traverseReadOnly(root, word);
        boolean found = node != null && node.isTerminal();
        notifySearch(word, found);
        return found;
    }

    public boolean isPrefix(String prefix) {
        if (!validator.isValid(prefix)) {
            return false;
        }
        return traversalEngine.traverseReadOnly(root, prefix) != null;
    }

    public int getSize() {
        return size;
    }

    public List<String> getAllWords() {
        return collectWordsByPredicate(root, (w, n) -> true, new StringBuilder());
    }

    public List<String> findWordsWithPrefix(String prefix) {
        if (!validator.isValid(prefix)) {
            return Collections.emptyList();
        }

        TrieNode node = traversalEngine.traverseReadOnly(root, prefix);
        if (node == null) {
            return Collections.emptyList();
        }

        return collectWordsByPredicate(node, (w, n) -> true, new StringBuilder(prefix));
    }

    public List<String> findWordsByFrequency(int minFrequency) {
        return collectWordsByPredicate(root,
            (w, n) -> n.getFrequency() >= minFrequency,
            new StringBuilder());
    }

    public OperationResult<Boolean> delete(String word) {
        if (isReadOnly) {
            return OperationResult.failure("Trie is in read-only mode");
        }

        OperationResult<Void> validation = validator.validate(word);
        if (!validation.isSuccess()) {
            return OperationResult.failure(validation.getMessage());
        }

        try {
            if (!searchInternal(word)) {
                return OperationResult.failure("Word not found");
            }

            TrieNode node = traversalEngine.traverseReadOnly(root, word);
            if (node != null) {
                node.unmarkAsWord();
                size--;
                notifyDelete(word);
                return OperationResult.success(true);
            }
            return OperationResult.failure("Deletion failed");
        } catch (Exception e) {
            return OperationResult.failure("Delete failed: " + e.getMessage());
        }
    }

    public TrieStatistics getStatistics() {
        int[] stats = computeStats(root, 0);
        return new TrieStatistics(size, stats[0], stats[1]);
    }

    public void forEach(Consumer<String> action) {
        getAllWords().forEach(action);
    }

    public void accept(TrieVisitor visitor) {
        acceptVisitor(root, visitor, "", 0);
    }

    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void clear() {
        if (isReadOnly) {
            throw new TrieException("Cannot clear read-only trie");
        }
        for (int i = 0; i < charset.getSize(); i++) {
            root.setChild(i, null);
        }
        size = 0;
        listeners.forEach(l -> l.onDelete("*"));
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

    public static void main(String[] args) {
        demonstrateBasicOperations();
        demonstrateAdvancedOperations();
        demonstrateListenerPattern();
        demonstrateStrategyPattern();
        demonstrateReadOnlyMode();
        demonstrateVisitorPattern();
        demonstrateErrorHandling();
    }

    private static void demonstrateBasicOperations() {
        System.out.println("=== Basic Operations ===");
        Trie trie = new Trie();
        String[] words = {"and", "ant", "do", "dad"};
        Arrays.stream(words).forEach(trie::insert);
        System.out.println("Inserted words: " + Arrays.toString(words));

        String[] searchKeys = {"do", "gee", "bat"};
        System.out.print("Search results: ");
        Arrays.stream(searchKeys)
            .forEach(key -> System.out.print(trie.search(key) ? "true " : "false "));

        System.out.print("\nPrefix results: ");
        String[] prefixKeys = {"ge", "ba", "do", "de"};
        Arrays.stream(prefixKeys)
            .forEach(prefix -> System.out.print(trie.isPrefix(prefix) ? "true " : "false "));
        System.out.println();
    }

    private static void demonstrateAdvancedOperations() {
        System.out.println("\n=== Advanced Operations ===");
        Trie trie = new Trie();
        String[] words = {"apple", "app", "apricot", "application"};
        Arrays.stream(words).forEach(trie::insert);

        System.out.println("All words: " + trie.getAllWords());
        System.out.println("Words with prefix 'app': " + trie.findWordsWithPrefix("app"));
        System.out.println("Trie size: " + trie.getSize());
        System.out.println("Statistics: " + trie.getStatistics());

        trie.delete("app");
        System.out.println("After deleting 'app': " + trie.getAllWords());
        System.out.println("Trie size: " + trie.getSize());
    }

    private static void demonstrateListenerPattern() {
        System.out.println("\n=== Listener Pattern Demo ===");
        Trie trie = new Trie();

        TrieOperationListener logger = new TrieOperationListener() {
            @Override
            public void onInsert(String word) {
                System.out.println("[LOG] Inserted: " + word);
            }

            @Override
            public void onDelete(String word) {
                System.out.println("[LOG] Deleted: " + word);
            }

            @Override
            public void onSearch(String word, boolean found) {
                System.out.println("[LOG] Searched: " + word + " -> " + (found ? "Found" : "Not Found"));
            }
        };

        trie.addListener(logger);
        OperationResult<Void> result = trie.insert("hello");
        System.out.println("Insert result: " + result.getMessage());
        trie.search("hello");
        OperationResult<Boolean> deleteResult = trie.delete("hello");
        System.out.println("Delete result: " + deleteResult.getMessage());
    }

    private static void demonstrateStrategyPattern() {
        System.out.println("\n=== Strategy Pattern Demo ===");
        Trie trie = new Trie();
        String[] words = {"test", "tree", "team", "teach"};
        Arrays.stream(words).forEach(w -> trie.insert(w));

        System.out.println("All words with 'te' prefix: " + trie.findWordsWithPrefix("te"));
        System.out.println("Statistics: " + trie.getStatistics());
    }

    private static void demonstrateReadOnlyMode() {
        System.out.println("\n=== Read-Only Mode Demo ===");
        Trie trie = new Trie();
        trie.insert("immutable");
        trie.insert("persistent");

        trie.setReadOnly(true);
        System.out.println("Trie set to read-only mode");
        System.out.println("All words: " + trie.getAllWords());

        OperationResult<Void> insertResult = trie.insert("new");
        System.out.println("Insert attempt result: " + insertResult.getMessage());
    }

    private static void demonstrateVisitorPattern() {
        System.out.println("\n=== Visitor Pattern Demo ===");
        Trie trie = new Trie();
        String[] words = {"apple", "app", "apex"};
        Arrays.stream(words).forEach(w -> trie.insert(w));

        System.out.println("Words with depth info:");
        trie.accept((word, node, depth) ->
            System.out.println("  " + word + " (depth=" + depth + ", freq=" + node.getFrequency() + ")")
        );
    }

    private static void demonstrateErrorHandling() {
        System.out.println("\n=== Error Handling Demo ===");
        Trie trie = new Trie();

        OperationResult<Void> result1 = trie.insert(null);
        System.out.println("Insert null: " + result1.getMessage());

        OperationResult<Void> result2 = trie.insert("");
        System.out.println("Insert empty: " + result2.getMessage());

        OperationResult<Boolean> deleteResult = trie.delete("nonexistent");
        System.out.println("Delete nonexistent: " + deleteResult.getMessage());
    }
}
