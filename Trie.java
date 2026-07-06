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
        private final CharacterMapping mapping;
        private final TrieNode[] children;
        boolean isEndOfWord;

        TrieNode(CharacterMapping mapping)
        {
            this.mapping = mapping;
            this.children = new TrieNode[mapping.size()];
        }

        boolean hasChild(char c) { return children[mapping.indexOf(c)] != null; }
        TrieNode getChild(char c) { return children[mapping.indexOf(c)]; }
        void setChild(char c, TrieNode node) { children[mapping.indexOf(c)] = node; }

        boolean hasAnyChild()
        {
            for (TrieNode child : children) {
                if (child != null) return true;
            }
            return false;
        }
    }

    private final CharacterMapping mapping;
    private final TrieNode root;

    public Trie() { this(LOWERCASE); }

    public Trie(CharacterMapping mapping)
    {
        this.mapping = mapping;
        this.root = new TrieNode(mapping);
    }

    public void insert(String key)
    {
        TrieNode curr = root;
        for (char c : key.toCharArray()) {
            if (!curr.hasChild(c)) {
                curr.setChild(c, new TrieNode(mapping));
            }
            curr = curr.getChild(c);
        }
        curr.isEndOfWord = true;
    }

    private TrieNode navigate(String key)
    {
        TrieNode curr = root;
        for (char c : key.toCharArray()) {
            curr = curr.getChild(c);
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

    public void delete(String key)
    {
        deleteFrom(root, key, 0);
    }

    private boolean deleteFrom(TrieNode node, String key, int depth)
    {
        if (node == null) return false;
        if (depth == key.length()) {
            if (!node.isEndOfWord) return false;
            node.isEndOfWord = false;
            return !node.hasAnyChild();
        }
        char c = key.charAt(depth);
        if (!deleteFrom(node.getChild(c), key, depth + 1)) return false;
        node.setChild(c, null);
        return !node.isEndOfWord && !node.hasAnyChild();
    }
}
