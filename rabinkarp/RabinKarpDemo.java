package rabinkarp;

import stringsearch.MatchResult;

public final class RabinKarpDemo {
    private RabinKarpDemo() {}

    public static void main(String[] args) {
        String txt = "geeksforgeeks";
        String pat = "geeks";

        for (int idx : SearchStrategy.RABIN_KARP.search(pat, txt))
            System.out.print(idx + " ");
        System.out.println();

        for (int idx : SearchStrategy.NAIVE.search(pat, txt))
            System.out.print(idx + " ");
        System.out.println();

        RabinKarpPattern compiled = RabinKarpPattern.compile(pat);
        MatchResult result = compiled.searchIn(txt);
        System.out.println(result.count() + " match(es): " + result.positions());
    }
}
