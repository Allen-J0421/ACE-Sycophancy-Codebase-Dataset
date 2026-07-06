class TrieNode {
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
