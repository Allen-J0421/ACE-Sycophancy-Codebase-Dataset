public class Trie {
    private static final int ALPHABET_SIZE = 26;
    private static final String LOWERCASE_ONLY_MESSAGE =
        "Trie only supports lowercase letters a-z: '";

    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        requireValidText(word);

        TrieNode node = findOrCreateNode(word);
        node.isWord = true;
    }

    public boolean search(String word) {
        requireValidText(word);

        TrieNode node = findNode(word);
        return node != null && node.isWord;
    }

    public boolean startsWith(String prefix) {
        requireValidText(prefix);
        return findNode(prefix) != null;
    }

    private TrieNode findOrCreateNode(String text) {
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
        TrieNode node = root;
        for (int i = 0; i < text.length(); i++) {
            int index = toIndex(text.charAt(i));
            node = node.children[index];
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    private static void requireValidText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        for (int i = 0; i < text.length(); i++) {
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
        private boolean isWord;
    }
}
