import java.util.function.Predicate;

public class Trie {
    private static final int ALPHABET_SIZE = 26;
    private static final String[] WORDS = {"and", "ant", "do", "dad"};
    private static final String[] SEARCH_KEYS = {"do", "gee", "bat"};
    private static final String[] PREFIX_KEYS = {"ge", "ba", "do", "de"};

    private static final class TrieNode {
        private final TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        private boolean isLeaf;

        private TrieNode childAt(char c) {
            return children[toIndex(c)];
        }

        private TrieNode ensureChild(char c) {
            int index = toIndex(c);
            if (children[index] == null) {
                children[index] = new TrieNode();
            }
            return children[index];
        }
    }

    private final TrieNode root = new TrieNode();

    public void insert(String key) {
        TrieNode node = ensurePath(key);
        node.isLeaf = true;
    }

    public boolean search(String key) {
        TrieNode node = findNode(key);
        return node != null && node.isLeaf;
    }

    public boolean isPrefix(String prefix) {
        return findNode(prefix) != null;
    }

    private TrieNode ensurePath(String key) {
        TrieNode node = root;
        for (char c : key.toCharArray()) {
            node = node.ensureChild(c);
        }
        return node;
    }

    private TrieNode findNode(String key) {
        TrieNode node = root;
        for (char c : key.toCharArray()) {
            node = node.childAt(c);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    private static int toIndex(char c) {
        return c - 'a';
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        for (String word : WORDS) {
            trie.insert(word);
        }

        System.out.println(formatMatches(SEARCH_KEYS, trie::search));
        System.out.print(formatMatches(PREFIX_KEYS, trie::isPrefix));
    }

    private static String formatMatches(String[] keys, Predicate<String> predicate) {
        StringBuilder output = new StringBuilder();
        for (String key : keys) {
            output.append(predicate.test(key) ? "true " : "false ");
        }
        return output.toString();
    }
}
