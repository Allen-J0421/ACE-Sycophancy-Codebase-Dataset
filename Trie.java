import java.util.Objects;

public final class Trie {
    private static final int ALPHABET_SIZE = 26;
    private static final char FIRST_LETTER = 'a';
    private static final char LAST_LETTER = 'z';

    private final TrieNode root = new TrieNode();

    /**
     * Adds a lowercase word to the trie.
     */
    public void insert(String word) {
        findOrCreateNode(word).markWord();
    }

    /**
     * Returns true when the trie contains the complete lowercase word.
     */
    public boolean contains(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isWord();
    }

    /**
     * Compatibility alias for {@link #contains(String)}.
     */
    public boolean search(String word) {
        return contains(word);
    }

    /**
     * Returns true when the trie contains the lowercase prefix.
     */
    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    /**
     * Compatibility alias for {@link #startsWith(String)}.
     */
    public boolean isPrefix(String prefix) {
        return startsWith(prefix);
    }

    private TrieNode findNode(String value) {
        return walk(value, false);
    }

    private TrieNode findOrCreateNode(String value) {
        return walk(value, true);
    }

    private TrieNode walk(String value, boolean createMissingNodes) {
        Objects.requireNonNull(value, "value must not be null");
        TrieNode current = root;

        for (int position = 0; position < value.length(); position++) {
            int index = toIndex(value.charAt(position), value);
            current = createMissingNodes
                ? current.getOrCreateChild(index)
                : current.getChild(index);
            if (current == null) {
                return null;
            }
        }

        return current;
    }

    private static int toIndex(char letter, String value) {
        if (letter < FIRST_LETTER || letter > LAST_LETTER) {
            throw new IllegalArgumentException(
                "Trie values may only contain lowercase letters a-z: " + value);
        }

        return letter - FIRST_LETTER;
    }

    private static final class TrieNode {
        private final TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        private boolean isWord;

        private TrieNode getChild(int index) {
            return children[index];
        }

        private TrieNode getOrCreateChild(int index) {
            if (children[index] == null) {
                children[index] = new TrieNode();
            }

            return children[index];
        }

        private boolean isWord() {
            return isWord;
        }

        private void markWord() {
            isWord = true;
        }
    }
}
