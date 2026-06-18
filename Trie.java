public class Trie {
    private static final int ALPHABET_SIZE = 26;

    private static final class TrieNode {
        private final TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        private boolean isLeaf;
    }

    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String key) {
        TrieNode node = root;
        for (char c : key.toCharArray()) {
            int index = toIndex(c);
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
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
            int index = toIndex(c);
            if (node.children[index] == null) {
                return null;
            }
            node = node.children[index];
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
        for (String key : searchKeys) {
            System.out.print(trie.search(key) ? "true " : "false ");
        }

        System.out.println();

        String[] prefixKeys = {"ge", "ba", "do", "de"};
        for (String key : prefixKeys) {
            System.out.print(trie.isPrefix(key) ? "true " : "false ");
        }
    }
}
