public class Trie {
    private static final char ALPHABET_START = 'a';
    private static final int ALPHABET_SIZE = 26;

    private static int charIndex(char c) { return c - ALPHABET_START; }

    private static class TrieNode {
        TrieNode[] children;
        boolean isEndOfWord;

        TrieNode() { children = new TrieNode[ALPHABET_SIZE]; }
    }

    private final TrieNode root = new TrieNode();

    public void insert(String key)
    {
        TrieNode curr = root;
        for (char c : key.toCharArray()) {
            int idx = charIndex(c);
            if (curr.children[idx] == null) {
                curr.children[idx] = new TrieNode();
            }
            curr = curr.children[idx];
        }
        curr.isEndOfWord = true;
    }

    private TrieNode navigate(String key)
    {
        TrieNode curr = root;
        for (char c : key.toCharArray()) {
            curr = curr.children[charIndex(c)];
            if (curr == null) return null;
        }
        return curr;
    }

    public boolean search(String key)
    {
        TrieNode node = navigate(key);
        return node != null && node.isEndOfWord;
    }

    public boolean isPrefix(String prefix)
    {
        return navigate(prefix) != null;
    }
}
