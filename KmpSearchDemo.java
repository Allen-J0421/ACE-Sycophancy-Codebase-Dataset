import java.util.List;

public final class KmpSearchDemo {

    private KmpSearchDemo() {
    }

    public static void main(String[] args) {
        String text = "aabaacaadaabaaba";
        String pattern = "aaba";

        List<Integer> matches = KmpSearch.search(pattern, text);

        for (int matchIndex : matches) {
            System.out.print(matchIndex + " ");
        }
    }
}
