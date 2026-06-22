import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NaivePatternSearch {

    private NaivePatternSearch() {}

    public static List<Integer> search(String pat, String txt) {
        if (pat == null || txt == null || pat.isEmpty() || pat.length() > txt.length()) {
            return Collections.emptyList();
        }

        int patLen = pat.length();
        int txtLen = txt.length();
        List<Integer> occurrences = new ArrayList<>();

        for (int i = 0; i <= txtLen - patLen; i++) {
            if (txt.regionMatches(i, pat, 0, patLen)) {
                occurrences.add(i);
            }
        }

        return occurrences;
    }

    public static void main(String[] args) {
        String txt = "aabaacaadaabaaba";
        String pat = "aaba";

        List<Integer> results = search(pat, txt);
        System.out.println(results);
    }
}
