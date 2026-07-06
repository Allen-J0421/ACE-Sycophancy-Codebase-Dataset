import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Trie implements Iterable<String> {
    public interface CharacterMapping {
        int indexOf(char c);
        char charAt(int index);
        int size();
    }

    private static final CharacterMapping LOWERCASE = new CharacterMapping() {
        public int indexOf(char c) { return c - 'a'; }
        public char charAt(int index) { return (char) ('a' + index); }
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
        TrieNode getChildAt(int index) { return children[index]; }
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

    @Override
    public Iterator<String> iterator() { return new TrieIterator(); }

    private class TrieIterator implements Iterator<String> {
        private class Frame {
            final TrieNode node;
            final int prefixLen;
            int childIdx;

            Frame(TrieNode node, int prefixLen) {
                this.node = node;
                this.prefixLen = prefixLen;
            }
        }

        private final Deque<Frame> stack = new ArrayDeque<>();
        private final StringBuilder prefix = new StringBuilder();
        private String pending;

        TrieIterator()
        {
            stack.push(new Frame(root, 0));
            advance();
        }

        private void advance()
        {
            pending = null;
            while (!stack.isEmpty() && pending == null) {
                Frame top = stack.peek();
                if (top.childIdx < mapping.size()) {
                    int idx = top.childIdx++;
                    TrieNode child = top.node.getChildAt(idx);
                    if (child == null) continue;
                    int savedLen = prefix.length();
                    prefix.append(mapping.charAt(idx));
                    stack.push(new Frame(child, savedLen));
                    if (child.isEndOfWord) pending = prefix.toString();
                } else {
                    stack.pop();
                    prefix.setLength(top.prefixLen);
                }
            }
        }

        @Override
        public boolean hasNext() { return pending != null; }

        @Override
        public String next()
        {
            if (pending == null) throw new NoSuchElementException();
            String result = pending;
            advance();
            return result;
        }
    }
}
