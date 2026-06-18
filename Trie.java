public class Trie {
    private static final int ALPHABET_SIZE = 26;
    private static final String LOWERCASE_ONLY_MESSAGE =
        "Trie only supports lowercase letters a-z: '";

    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode node = findOrCreateNode(word);
        node.terminal = true;
    }

    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.terminal;
    }

    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    private TrieNode findOrCreateNode(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        TrieNode node = root;
        for (int i = 0; i < text.length(); i++) {
            int index = toIndex(text.charAt(i));
            TrieNode next = node.children[index];
            if (next == null) {
                next = new TrieNode();
                node.children[index] = next;
            }
            node = next;
        }
        return node;
    }

    private TrieNode findNode(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        TrieNode node = root;
        for (int i = 0; i < text.length(); i++) {
            int index = toIndex(text.charAt(i));
            node = node.children[index];
            if (node == null) {
                validateSuffix(text, i + 1);
                return null;
            }
        }
        return node;
    }

    private static void validateSuffix(String text, int startIndex) {
        for (int i = startIndex; i < text.length(); i++) {
            toIndex(text.charAt(i));
        }
    }

    private static int toIndex(char c) {
        if (c < 'a' || c > 'z') {
            throw new IllegalArgumentException(LOWERCASE_ONLY_MESSAGE + c + "'");
        }
        return c - 'a';
    }

    private static final class TrieNode {
        private final TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        private boolean terminal;
    }
}
