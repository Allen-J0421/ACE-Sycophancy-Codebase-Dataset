import java.util.List;

public final class RabinKarpDemo {

    private RabinKarpDemo() {
        // Demo entry point only.
    }

    public static void main(String[] args) {
        String text = "geeksforgeeks";
        String pattern = "geeks";

        List<Integer> matches = RabinKarp.search(pattern, text);
        for (int index : matches) {
            System.out.print(index + " ");
        }
        System.out.println();
    }
}
