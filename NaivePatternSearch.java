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
            if (matchesAt(pat, txt, i)) {
                occurrences.add(i);
            }
        }

        return occurrences;
    }

    private static boolean matchesAt(String pat, String txt, int offset) {
        for (int j = 0; j < pat.length(); j++) {
            if (txt.charAt(offset + j) != pat.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String txt = "aabaacaadaabaaba";
        String pat = "aaba";

        List<Integer> results = search(pat, txt);
        System.out.println(results);
    }
}
