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

        private void markAsLeaf() {
            isLeaf = true;
        }

        private boolean isLeaf() {
            return isLeaf;
        }
    }

    @FunctionalInterface
    private interface NodeTransition {
        TrieNode next(TrieNode node, char c);
    }

    private final TrieNode root = new TrieNode();

    public void insert(String key) {
        validateKey(key);
        TrieNode node = ensurePath(key);
        node.markAsLeaf();
    }

    public void insertAll(String... keys) {
        for (String key : keys) {
            insert(key);
        }
    }

    public boolean search(String key) {
        validateKey(key);
        TrieNode node = findNode(key);
        return node != null && node.isLeaf();
    }

    public boolean isPrefix(String prefix) {
        validateKey(prefix);
        return findNode(prefix) != null;
    }

    private TrieNode ensurePath(String key) {
        return walk(key, (node, c) -> node.ensureChild(c));
    }

    private TrieNode findNode(String key) {
        return walk(key, (node, c) -> node.childAt(c));
    }

    private TrieNode walk(String key, NodeTransition transition) {
        TrieNode node = root;
        for (int i = 0; i < key.length(); i++) {
            node = transition.next(node, key.charAt(i));
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    private static int toIndex(char c) {
        return c - 'a';
    }

    private static void validateKey(String key) {
        Objects.requireNonNull(key, "key");
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c < 'a' || c > 'z') {
                throw new IllegalArgumentException(
                    "Keys must contain only lowercase letters a-z: " + key);
            }
        }
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
