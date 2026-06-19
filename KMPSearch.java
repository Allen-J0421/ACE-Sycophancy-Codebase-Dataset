import java.util.List;

public final class KMPSearch {

    private KMPSearch() {
        // Utility class.
    }

    public static void main(String[] args) {
        String text = "aabaacaadaabaaba";
        String pattern = "aaba";

        List<Integer> matches = KmpMatcher.findAllMatches(pattern, text);
        System.out.println(KmpFormatter.joinMatches(matches));
    }
}
