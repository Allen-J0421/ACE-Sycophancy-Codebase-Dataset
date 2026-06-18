import java.util.*;
import java.util.stream.Collectors;

class TrieNode {
    private static final int ALPHABET_SIZE = 26;
    private final TrieNode[] children;
    private boolean isWord;

    TrieNode() {
        this.children = new TrieNode[ALPHABET_SIZE];
        this.isWord = false;
    }

    TrieNode getChild(int index) {
        return children[index];
    }

    void setChild(int index, TrieNode node) {
        children[index] = node;
    }

    void markAsWord() {
        this.isWord = true;
    }

    void unmarkAsWord() {
        this.isWord = false;
    }

    boolean isTerminal() {
        return isWord;
    }

    boolean hasChild(int index) {
        return children[index] != null;
    }

    static int charToIndex(char c) {
        return c - 'a';
    }

    static boolean isValidChar(char c) {
        return Character.isLowerCase(c);
    }
}

class Trie {
    private final TrieNode root;
    private int size;

    public Trie() {
        this.root = new TrieNode();
        this.size = 0;
    }

    public void insert(String word) {
        if (!isValidInput(word)) {
            throw new IllegalArgumentException("Word must not be null or empty");
        }

        if (!search(word)) {
            traverseAndInsert(word);
            size++;
        }
    }

    public boolean search(String word) {
        if (!isValidInput(word)) {
            return false;
        }

        TrieNode node = traverse(word, false);
        return node != null && node.isTerminal();
    }

    public boolean isPrefix(String prefix) {
        if (!isValidInput(prefix)) {
            return false;
        }

        return traverse(prefix, false) != null;
    }

    public int getSize() {
        return size;
    }

    public List<String> getAllWords() {
        List<String> words = new ArrayList<>();
        dfs(root, new StringBuilder(), words);
        return words;
    }

    public List<String> findWordsWithPrefix(String prefix) {
        if (!isValidInput(prefix)) {
            return Collections.emptyList();
        }

        TrieNode node = traverse(prefix, false);
        if (node == null) {
            return Collections.emptyList();
        }

        List<String> words = new ArrayList<>();
        dfs(node, new StringBuilder(prefix), words);
        return words;
    }

    public boolean delete(String word) {
        if (!isValidInput(word)) {
            return false;
        }

        if (!search(word)) {
            return false;
        }

        deleteHelper(root, word, 0);
        size--;
        return true;
    }

    private boolean isValidInput(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        for (char c : input.toCharArray()) {
            if (!TrieNode.isValidChar(c)) {
                return false;
            }
        }
        return true;
    }

    private void traverseAndInsert(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            int index = TrieNode.charToIndex(c);
            if (!curr.hasChild(index)) {
                curr.setChild(index, new TrieNode());
            }
            curr = curr.getChild(index);
        }
        curr.markAsWord();
    }

    private TrieNode traverse(String word, boolean createIfMissing) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            int index = TrieNode.charToIndex(c);
            if (!curr.hasChild(index)) {
                if (!createIfMissing) {
                    return null;
                }
                curr.setChild(index, new TrieNode());
            }
            curr = curr.getChild(index);
        }
        return curr;
    }

    private void dfs(TrieNode node, StringBuilder current, List<String> words) {
        if (node.isTerminal()) {
            words.add(current.toString());
        }

        for (int i = 0; i < 26; i++) {
            if (node.getChild(i) != null) {
                current.append((char) ('a' + i));
                dfs(node.getChild(i), current, words);
                current.deleteCharAt(current.length() - 1);
            }
        }
    }

    private boolean deleteHelper(TrieNode node, String word, int index) {
        if (index == word.length()) {
            if (!node.isTerminal()) {
                return false;
            }
            node.unmarkAsWord();
            return true;
        }

        int childIndex = TrieNode.charToIndex(word.charAt(index));
        TrieNode child = node.getChild(childIndex);

        if (child == null) {
            return false;
        }

        deleteHelper(child, word, index + 1);
        return child.isTerminal() || hasNonEmptyChild(child);
    }

    private boolean hasNonEmptyChild(TrieNode node) {
        for (int i = 0; i < 26; i++) {
            if (node.getChild(i) != null) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        demonstrateBasicOperations();
        demonstrateAdvancedOperations();
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

        trie.delete("app");
        System.out.println("After deleting 'app': " + trie.getAllWords());
        System.out.println("Trie size: " + trie.getSize());
    }
}
