import java.util.ArrayList;
import java.util.List;

class IteratorTest {
    static void check(String label, Object got, Object want) {
        if (!got.equals(want)) throw new AssertionError(label + ": got " + got + ", want " + want);
        System.out.println("OK " + label);
    }

    public static void main(String[] args) {
        Trie t = new Trie();
        t.insert("and"); t.insert("ant"); t.insert("do"); t.insert("dad");

        // collect via for-each
        List<String> words = new ArrayList<>();
        for (String w : t) words.add(w);
        check("count",      words.size(), 4);
        check("word[0]",    words.get(0), "and");
        check("word[1]",    words.get(1), "ant");
        check("word[2]",    words.get(2), "dad");
        check("word[3]",    words.get(3), "do");

        // iterator survives a delete mid-build (separate iterator)
        t.delete("ant");
        words.clear();
        for (String w : t) words.add(w);
        check("after delete count", words.size(), 3);
        check("after delete [0]",   words.get(0), "and");
        check("after delete [1]",   words.get(1), "dad");
        check("after delete [2]",   words.get(2), "do");

        // empty trie
        Trie empty = new Trie();
        check("empty hasNext", empty.iterator().hasNext(), false);

        // single word
        Trie one = new Trie();
        one.insert("z");
        words.clear();
        for (String w : one) words.add(w);
        check("single word", words.get(0), "z");

        // prefix relationship preserved in order
        Trie pre = new Trie();
        pre.insert("an"); pre.insert("and"); pre.insert("ant");
        words.clear();
        for (String w : pre) words.add(w);
        check("prefix first [0]", words.get(0), "an");
        check("prefix first [1]", words.get(1), "and");
        check("prefix first [2]", words.get(2), "ant");
    }
}
