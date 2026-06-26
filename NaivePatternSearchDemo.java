import java.util.List;

public final class NaivePatternSearchDemo {

    private NaivePatternSearchDemo() {
        // Demo entry point.
    }

    public static void main(String[] args) {
        String text = "aabaacaadaabaaba";
        String pattern = "aaba";

        List<Integer> matches = NaivePatternSearch.search(pattern, text);
        for (int index : matches) {
            System.out.print(index + " ");
        }
    }
}
