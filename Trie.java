public class Trie {
    private static final int ALPHABET_SIZE = 26;

    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int index = toIndex(c);
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
        }
        node.isWord = true;
    }

    public boolean search(String word) {
        TrieNode node = traverse(word);
        return node != null && node.isWord;
    }

    public boolean isPrefix(String prefix) {
        return traverse(prefix) != null;
    }

    public boolean startsWith(String prefix) {
        return isPrefix(prefix);
    }

    private TrieNode traverse(String text) {
        TrieNode node = root;
        for (char c : text.toCharArray()) {
            int index = toIndex(c);
            node = node.children[index];
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    private static int toIndex(char c) {
        if (c < 'a' || c > 'z') {
            throw new IllegalArgumentException(
                "Trie only supports lowercase letters a-z: '" + c + "'");
        }
        return c - 'a';
    }

    private static final class TrieNode {
        private final TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        private boolean isWord;
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        String[] words = {"and", "ant", "do", "dad"};
        for (String word : words) {
            trie.insert(word);
        }

        String[] searchKeys = {"do", "gee", "bat"};
        for (String key : searchKeys) {
            System.out.print(trie.search(key) + " ");
        }
        System.out.println();

        String[] prefixKeys = {"ge", "ba", "do", "de"};
        for (String prefix : prefixKeys) {
            System.out.print(trie.isPrefix(prefix) + " ");
        }
    }
}
