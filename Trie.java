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

    private enum TraversalMode {
        CREATE_MISSING {
            @Override
            TrieNode next(TrieNode node, int index) {
                return node.ensureChild(index);
            }
        },
        EXISTING_ONLY {
            @Override
            TrieNode next(TrieNode node, int index) {
                return node.childAt(index);
            }
        };

        abstract TrieNode next(TrieNode node, int index);
    }

    private final TrieNode root = new TrieNode();

    public void insert(String key) {
        TrieNode node = ensurePath(key);
        node.markAsLeaf();
    }

    public void insertAll(String... keys) {
        for (String key : keys) {
            insert(key);
        }
    }

    public boolean search(String key) {
        TrieNode node = findNode(key);
        return node != null && node.isLeaf();
    }

    public boolean isPrefix(String prefix) {
        return findNode(prefix) != null;
    }

    private TrieNode ensurePath(String key) {
        return walk(key, TraversalMode.CREATE_MISSING);
    }

    private TrieNode findNode(String key) {
        return walk(key, TraversalMode.EXISTING_ONLY);
    }

    private TrieNode walk(String key, TraversalMode mode) {
        Objects.requireNonNull(key, "key");
        TrieNode node = root;
        for (int i = 0; i < key.length(); i++) {
            int index = toIndex(key.charAt(i), key);
            node = mode.next(node, index);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    private static int toIndex(char c, String key) {
        if (c < 'a' || c > 'z') {
            throw new IllegalArgumentException(
                "Keys must contain only lowercase letters a-z: " + key);
        }
        return c - 'a';
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
