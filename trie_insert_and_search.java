class TrieDemo {
    public static void main(String[] args)
    {
        Trie trie = new Trie();
        String[] arr = {"and", "ant", "do", "dad"};
        for (String s : arr) {
            trie.insert(s);
        }
        String[] searchKeys = {"do", "gee", "bat"};
        for (String s : searchKeys) {
            System.out.print(trie.search(s) + " ");
        }
        System.out.println();
        String[] prefixKeys = {"ge", "ba", "do", "de"};
        for (String s : prefixKeys) {
            System.out.print(trie.isPrefix(s) + " ");
        }
    }
}
