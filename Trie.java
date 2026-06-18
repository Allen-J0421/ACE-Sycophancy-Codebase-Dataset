import java.util.Objects;
import java.util.function.Predicate;

public class Trie {
    private static final int ALPHABET_SIZE = 26;
    private static final String[] WORDS = {"and", "ant", "do", "dad"};
    private static final String[] SEARCH_KEYS = {"do", "gee", "bat"};
    private static final String[] PREFIX_KEYS = {"ge", "ba", "do", "de"};

    private static final class TrieNode {
        private final TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        private boolean isLeaf;

        private TrieNode childAt(int index) {
            return children[index];
        }

        private TrieNode ensureChild(int index) {
            if (children[index] == null) {
                children[index] = new TrieNode();
            }
            return children[index];
        }

        private void markAsLeaf() {
            isLeaf = true;
        }

        private boolean isLeaf() {
            return isLeaf;
        }
    }

    @FunctionalInterface
    private interface NodeTransition {
        TrieNode next(TrieNode node, int index);
    }

    private final TrieNode root = new TrieNode();

    public void insert(String key) {
        TrieNode node = ensurePath(parseKey(key));
        node.markAsLeaf();
    }

    public void insertAll(String... keys) {
        for (String key : keys) {
            insert(key);
        }
    }

    public boolean search(String key) {
        TrieNode node = findNode(parseKey(key));
        return node != null && node.isLeaf();
    }

    public boolean isPrefix(String prefix) {
        return findNode(parseKey(prefix)) != null;
    }

    private TrieNode ensurePath(int[] indexes) {
        return walk(indexes, (node, index) -> node.ensureChild(index));
    }

    private TrieNode findNode(int[] indexes) {
        return walk(indexes, (node, index) -> node.childAt(index));
    }

    private TrieNode walk(int[] indexes, NodeTransition transition) {
        TrieNode node = root;
        for (int index : indexes) {
            node = transition.next(node, index);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    private static int[] parseKey(String key) {
        Objects.requireNonNull(key, "key");
        int[] indexes = new int[key.length()];
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c < 'a' || c > 'z') {
                throw new IllegalArgumentException(
                    "Keys must contain only lowercase letters a-z: " + key);
            }
            indexes[i] = c - 'a';
        }
        return indexes;
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insertAll(WORDS);

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
