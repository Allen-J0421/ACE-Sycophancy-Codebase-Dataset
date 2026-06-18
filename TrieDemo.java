import java.util.function.Predicate;

final class TrieDemo {
    private TrieDemo() {
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        insertAll(trie, "and", "ant", "do", "dad");
        printResults(trie::search, "do", "gee", "bat");
        printResults(trie::startsWith, "ge", "ba", "do", "de");
    }

    private static void insertAll(Trie trie, String... words) {
        for (String word : words) {
            trie.insert(word);
        }
    }

    private static void printResults(Predicate<String> lookup, String... values) {
        for (String value : values) {
            System.out.print(lookup.test(value) + " ");
        }
        System.out.println();
    }
}
