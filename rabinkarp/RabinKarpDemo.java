package rabinkarp;

import java.util.List;

public final class RabinKarpDemo {
    private RabinKarpDemo() {}

    public static void main(String[] args) {
        String txt = "geeksforgeeks";
        String pat = "geeks";

        List<Integer> res = RabinKarp.search(pat, txt);
        for (int idx : res)
            System.out.print(idx + " ");
        System.out.println();

        RabinKarpPattern compiled = RabinKarpPattern.compile(pat);
        List<Integer> res2 = compiled.searchIn(txt);
        for (int idx : res2)
            System.out.print(idx + " ");
        System.out.println();
    }
}
