import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NaivePatternSearch {

    private NaivePatternSearch() {}

    public static List<Integer> search(String pattern, String text) {
        if (pattern == null || text == null || pattern.isEmpty() || pattern.length() > text.length()) {
            return Collections.emptyList();
        }

        int patternLen = pattern.length();
        List<Integer> occurrences = new ArrayList<>();

        for (int i = 0; i <= text.length() - patternLen; i++) {
            if (text.regionMatches(i, pattern, 0, patternLen)) {
                occurrences.add(i);
            }
        }

        return occurrences;
    }

    public static void main(String[] args) {
        String text = "aabaacaadaabaaba";
        String pattern = "aaba";

        List<Integer> results = search(pattern, text);
        System.out.println(results);
    }
}
