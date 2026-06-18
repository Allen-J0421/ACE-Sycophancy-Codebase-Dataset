import java.util.function.Predicate;

public class Trie {
    private static final int ALPHABET_SIZE = 26;
    private static final String[] WORDS = {"and", "ant", "do", "dad"};
    private static final String[] SEARCH_KEYS = {"do", "gee", "bat"};
    private static final String[] PREFIX_KEYS = {"ge", "ba", "do", "de"};

    private static final class TrieNode {
        private final TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        private boolean isLeaf;

        private TrieNode next(char c, boolean createIfMissing) {
            int index = toIndex(c);
            if (children[index] == null && createIfMissing) {
                children[index] = new TrieNode();
            }
            return children[index];
        }
    }

    private final TrieNode root = new TrieNode();

    public void insert(String key) {
        TrieNode node = traverse(key, true);
        node.isLeaf = true;
    }

    public boolean search(String key) {
        TrieNode node = traverse(key, false);
        return node != null && node.isLeaf;
    }

    public boolean isPrefix(String prefix) {
        return traverse(prefix, false) != null;
    }

    private TrieNode traverse(String key, boolean createIfMissing) {
        TrieNode node = root;
        for (char c : key.toCharArray()) {
            node = node.next(c, createIfMissing);
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

        printMatches(SEARCH_KEYS, trie::search);
        System.out.println();
        printMatches(PREFIX_KEYS, trie::isPrefix);
    }

    private static void printMatches(String[] keys, Predicate<String> predicate) {
        for (String key : keys) {
            System.out.print(predicate.test(key) ? "true " : "false ");
        }
    }
}
