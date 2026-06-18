import java.util.*;
import java.util.function.Consumer;

interface CharacterValidator {
    boolean isValid(char c);
}

interface TrieOperationListener {
    void onInsert(String word);
    void onDelete(String word);
    void onSearch(String word, boolean found);
}

class CharacterSet {
    private final int size;
    private final int offset;

    CharacterSet(int size, int offset) {
        this.size = size;
        this.offset = offset;
    }

    int toIndex(char c) {
        return c - offset;
    }

    char fromIndex(int index) {
        return (char) (index + offset);
    }

    int getSize() {
        return size;
    }

    static CharacterSet LOWERCASE = new CharacterSet(26, 'a');
}

class TrieNode {
    private final TrieNode[] children;
    private boolean isWord;
    private int frequency;

    TrieNode(CharacterSet charset) {
        this.children = new TrieNode[charset.getSize()];
        this.isWord = false;
        this.frequency = 0;
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
    }

    void unmarkAsWord() {
        this.isWord = false;
        this.frequency = Math.max(0, frequency - 1);
    }

    boolean isTerminal() {
        return isWord;
    }

    int getFrequency() {
        return frequency;
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
}

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
            if (!isValidChar(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidChar(char c) {
        int index = charset.toIndex(c);
        return index >= 0 && index < charset.getSize();
    }
}

class TrieStatistics {
    private int totalWords;
    private int totalNodes;
    private int maxDepth;

    TrieStatistics(int totalWords, int totalNodes, int maxDepth) {
        this.totalWords = totalWords;
        this.totalNodes = totalNodes;
        this.maxDepth = maxDepth;
    }

    int getTotalWords() {
        return totalWords;
    }

    int getTotalNodes() {
        return totalNodes;
    }

    int getMaxDepth() {
        return maxDepth;
    }

    @Override
    public String toString() {
        return String.format("TrieStatistics{words=%d, nodes=%d, maxDepth=%d}", totalWords, totalNodes, maxDepth);
    }
}

class Trie {
    private final TrieNode root;
    private final CharacterSet charset;
    private final InputValidator validator;
    private final List<TrieOperationListener> listeners;
    private int size;

    public Trie() {
        this(CharacterSet.LOWERCASE);
    }

    public Trie(CharacterSet charset) {
        this.charset = charset;
        this.root = new TrieNode(charset);
        this.validator = new InputValidator(charset);
        this.listeners = new ArrayList<>();
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
            traverseAndInsert(word);
            size++;
            notifyInsert(word);
        }
    }

    public boolean search(String word) {
        if (!validator.isValid(word)) {
            return false;
        }

        TrieNode node = traverse(word, false);
        boolean found = node != null && node.isTerminal();
        notifySearch(word, found);
        return found;
    }

    public boolean isPrefix(String prefix) {
        if (!validator.isValid(prefix)) {
            return false;
        }
        return traverse(prefix, false) != null;
    }

    public int getSize() {
        return size;
    }

    public List<String> getAllWords() {
        return collectWords(root, new StringBuilder());
    }

    public List<String> findWordsWithPrefix(String prefix) {
        if (!validator.isValid(prefix)) {
            return Collections.emptyList();
        }

        TrieNode node = traverse(prefix, false);
        if (node == null) {
            return Collections.emptyList();
        }

        return collectWords(node, new StringBuilder(prefix));
    }

    public boolean delete(String word) {
        if (!validator.isValid(word)) {
            return false;
        }

        if (!search(word)) {
            return false;
        }

        deleteHelper(root, word, 0);
        size--;
        notifyDelete(word);
        return true;
    }

    public TrieStatistics getStatistics() {
        int[] stats = computeStats(root, 0);
        return new TrieStatistics(size, stats[0], stats[1]);
    }

    public void forEach(Consumer<String> action) {
        getAllWords().forEach(action);
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

    private List<String> collectWords(TrieNode node, StringBuilder current) {
        List<String> words = new ArrayList<>();
        dfs(node, current, words);
        return words;
    }

    private void traverseAndInsert(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            int index = charset.toIndex(c);
            if (!curr.hasChild(index)) {
                curr.setChild(index, new TrieNode(charset));
            }
            curr = curr.getChild(index);
        }
        curr.markAsWord();
    }

    private TrieNode traverse(String word, boolean createIfMissing) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            int index = charset.toIndex(c);
            if (!curr.hasChild(index)) {
                if (!createIfMissing) {
                    return null;
                }
                curr.setChild(index, new TrieNode(charset));
            }
            curr = curr.getChild(index);
        }
        return curr;
    }

    private void dfs(TrieNode node, StringBuilder current, List<String> words) {
        if (node.isTerminal()) {
            words.add(current.toString());
        }

        for (int i = 0; i < charset.getSize(); i++) {
            if (node.hasChild(i)) {
                current.append(charset.fromIndex(i));
                dfs(node.getChild(i), current, words);
                current.deleteCharAt(current.length() - 1);
            }
        }
    }

    private void deleteHelper(TrieNode node, String word, int index) {
        if (index == word.length()) {
            node.unmarkAsWord();
            return;
        }

        int childIndex = charset.toIndex(word.charAt(index));
        TrieNode child = node.getChild(childIndex);

        if (child != null) {
            deleteHelper(child, word, index + 1);
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
}
