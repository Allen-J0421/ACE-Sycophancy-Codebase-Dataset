import java.util.Iterator;

public class Trie implements Iterable<String> {
    private static final CharacterMapping LOWERCASE = new CharacterMapping() {
        public int indexOf(char c) { return c - 'a'; }
        public char charAt(int index) { return (char) ('a' + index); }
        public int size() { return 26; }
    };

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

    @Override
    public Iterator<String> iterator()
    {
        return new TrieIterator(root, mapping);
    }
}
