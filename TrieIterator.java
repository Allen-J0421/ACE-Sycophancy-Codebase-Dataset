import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

class TrieIterator implements Iterator<String> {
    private static class Frame {
        final TrieNode node;
        final int prefixLen;
        int childIdx;

        Frame(TrieNode node, int prefixLen)
        {
            this.node = node;
            this.prefixLen = prefixLen;
        }
    }

    private final CharacterMapping mapping;
    private final Deque<Frame> stack = new ArrayDeque<>();
    private final StringBuilder prefix = new StringBuilder();
    private String pending;

    TrieIterator(TrieNode root, CharacterMapping mapping)
    {
        this.mapping = mapping;
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
