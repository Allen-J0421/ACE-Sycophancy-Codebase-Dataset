public class Trie {
    private static final int ALPHABET_SIZE = 26;

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

    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String key) {
        TrieNode node = root;
        for (char c : key.toCharArray()) {
            node = node.ensureChild(c);
        }
        node.isLeaf = true;
    }

    public boolean search(String key) {
        TrieNode node = findNode(key);
        return node != null && node.isLeaf;
    }

    public boolean isPrefix(String prefix) {
        return findNode(prefix) != null;
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
        String[] words = {"and", "ant", "do", "dad"};
        for (String word : words) {
            trie.insert(word);
        }

        String[] searchKeys = {"do", "gee", "bat"};
        printMatches(searchKeys, trie::search);
        System.out.println();

        String[] prefixKeys = {"ge", "ba", "do", "de"};
        printMatches(prefixKeys, trie::isPrefix);
    }

    private static void printMatches(String[] keys, KeyPredicate predicate) {
        for (String key : keys) {
            System.out.print(predicate.test(key) ? "true " : "false ");
        }
    }

    @FunctionalInterface
    private interface KeyPredicate {
        boolean test(String key);
    }
}
