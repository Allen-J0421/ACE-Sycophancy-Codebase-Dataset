import java.util.Objects;

public final class Trie {
    private static final int ALPHABET_SIZE = 26;
    private static final char FIRST_LETTER = 'a';
    private static final char LAST_LETTER = 'z';

    private final TrieNode root = new TrieNode();

    public void insert(String key) {
        Objects.requireNonNull(key, "key must not be null");
        TrieNode current = root;

        for (int position = 0; position < key.length(); position++) {
            int index = toIndex(key.charAt(position), key);
            current = current.getOrCreateChild(index);
        }

        current.markWord();
    }

    public boolean search(String key) {
        TrieNode node = findNode(key);
        return node != null && node.isWord();
    }

    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    public boolean isPrefix(String prefix) {
        return startsWith(prefix);
    }

    private TrieNode findNode(String key) {
        Objects.requireNonNull(key, "key must not be null");
        TrieNode current = root;

        for (int position = 0; position < key.length(); position++) {
            current = current.getChild(toIndex(key.charAt(position), key));
            if (current == null) {
                return null;
            }
        }

        return current;
    }

    private static int toIndex(char letter, String key) {
        if (letter < FIRST_LETTER || letter > LAST_LETTER) {
            throw new IllegalArgumentException(
                "Trie keys may only contain lowercase letters a-z: " + key);
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
