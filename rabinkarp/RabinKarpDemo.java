package rabinkarp;

import java.util.List;

public final class RabinKarpDemo {
    private RabinKarpDemo() {}

    public static void main(String[] args) {
        String txt = "geeksforgeeks";
        String pat = "geeks";

        StringMatcher rk = StringMatcherFactory.rabinKarp();
        for (int idx : rk.search(pat, txt))
            System.out.print(idx + " ");
        System.out.println();

        StringMatcher naive = StringMatcherFactory.naive();
        for (int idx : naive.search(pat, txt))
            System.out.print(idx + " ");
        System.out.println();

        RabinKarpPattern compiled = RabinKarpPattern.compile(pat);
        for (int idx : compiled.searchIn(txt))
            System.out.print(idx + " ");
        System.out.println();
    }
}
