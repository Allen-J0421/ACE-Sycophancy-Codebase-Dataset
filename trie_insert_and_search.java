import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    InputValidator(CharacterSet charset) {
        this.charset = charset;
    }

    boolean isValid(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        for (char c : input.toCharArray()) {
            if (!charset.isValid(c)) {
                return false;
            }
        }
        return true;
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

// Main Trie implementation with visitor pattern
class Trie {
    private final TrieNode root;
    private final CharacterSet charset;
    private final InputValidator validator;
    private final List<TrieOperationListener> listeners;
    private final Stack<TrieOperation> operationHistory;
    private int size;

    public Trie() {
        this(new LowercaseCharacterSet());
    }

    public Trie(CharacterSet charset) {
        this.charset = charset;
        this.root = new TrieNode(charset.getSize());
        this.validator = new InputValidator(charset);
        this.listeners = new ArrayList<>();
        this.operationHistory = new Stack<>();
        this.size = 0;
    }

    public void addListener(TrieOperationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TrieOperationListener listener) {
        listeners.remove(listener);
    }

    public void insert(String word) {
        if (!validator.isValid(word)) {
            throw new IllegalArgumentException("Invalid word: " + word);
        }

        if (!search(word)) {
            TrieNode node = traverse(word, new CreatingTraversalStrategy(charset));
            node.markAsWord();
            size++;
            notifyInsert(word);
            operationHistory.push(() -> node.unmarkAsWord());
        }
    }

    public boolean search(String word) {
        if (!validator.isValid(word)) {
            return false;
        }

        TrieNode node = traverse(word, new ReadOnlyTraversalStrategy(charset));
        boolean found = node != null && node.isTerminal();
        notifySearch(word, found);
        return found;
    }

    public boolean isPrefix(String prefix) {
        if (!validator.isValid(prefix)) {
            return false;
        }
        return traverse(prefix, new ReadOnlyTraversalStrategy(charset)) != null;
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

        TrieNode node = traverse(prefix, new ReadOnlyTraversalStrategy(charset));
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

    public boolean delete(String word) {
        if (!validator.isValid(word)) {
            return false;
        }

        if (!search(word)) {
            return false;
        }

        TrieNode node = traverse(word, new ReadOnlyTraversalStrategy(charset));
        if (node != null) {
            node.unmarkAsWord();
            size--;
            notifyDelete(word);
            return true;
        }
        return false;
    }

    public TrieStatistics getStatistics() {
        int[] stats = computeStats(root, 0);
        return new TrieStatistics(size, stats[0], stats[1]);
    }

    public void forEach(Consumer<String> action) {
        getAllWords().forEach(action);
    }

    public void clear() {
        root.setChild(0, null);
        for (int i = 1; i < charset.getSize(); i++) {
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

    private TrieNode traverse(String input, TrieTraversalStrategy strategy) {
        return strategy.traverse(root, input);
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
        trie.insert("hello");
        trie.search("hello");
        trie.delete("hello");
    }

    private static void demonstrateStrategyPattern() {
        System.out.println("\n=== Strategy Pattern Demo ===");
        Trie trie = new Trie();
        String[] words = {"test", "tree", "team", "teach"};
        Arrays.stream(words).forEach(trie::insert);

        System.out.println("All words with 'te' prefix: " + trie.findWordsWithPrefix("te"));
        System.out.println("Statistics: " + trie.getStatistics());
    }
}
