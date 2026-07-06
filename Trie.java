public class Trie {
    public interface CharacterMapping {
        int indexOf(char c);
        int size();
    }

    private static final CharacterMapping LOWERCASE = new CharacterMapping() {
        public int indexOf(char c) { return c - 'a'; }
        public int size() { return 26; }
    };

    private static class TrieNode {
        TrieNode[] children;
        boolean isEndOfWord;

        TrieNode(int size) { children = new TrieNode[size]; }
    }

    private final CharacterMapping mapping;
    private final TrieNode root;

    public Trie() { this(LOWERCASE); }

    public Trie(CharacterMapping mapping)
    {
        this.mapping = mapping;
        this.root = new TrieNode(mapping.size());
    }

    public void insert(String key)
    {
        TrieNode curr = root;
        for (char c : key.toCharArray()) {
            int idx = mapping.indexOf(c);
            if (curr.children[idx] == null) {
                curr.children[idx] = new TrieNode(mapping.size());
            }
            curr = curr.children[idx];
        }
        curr.isEndOfWord = true;
    }

    private TrieNode navigate(String key)
    {
        TrieNode curr = root;
        for (char c : key.toCharArray()) {
            curr = curr.children[mapping.indexOf(c)];
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
