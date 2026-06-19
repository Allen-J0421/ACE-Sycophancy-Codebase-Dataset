package rabinkarp;

import java.util.List;

public final class RabinKarpDemo {

    private RabinKarpDemo() {
        // Demo entry point only.
    }

    public static void main(String[] args) {
        String text = args.length > 0 ? args[0] : "geeksforgeeks";
        String pattern = args.length > 1 ? args[1] : "geeks";

        List<Integer> matches = RabinKarpPattern.compile(pattern).searchIn(text);
        for (int index : matches) {
            System.out.print(index + " ");
        }
        System.out.println();
    }
}
