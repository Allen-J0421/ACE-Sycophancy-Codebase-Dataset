import java.util.List;

public class RabinKarpDemo {

    public static void main(String[] args) {
        String text = "geeksforgeeks";
        String pattern = "geeks";

        RabinKarpMatcher matcher = new RabinKarp(pattern);
        List<Integer> matches = matcher.findMatches(text);

        System.out.printf("Pattern \"%s\" found at positions: ", pattern);
        for (int index : matches) {
            System.out.print(index + " ");
        }
        System.out.println();
    }
}
