import java.util.ArrayList;
import java.util.List;

public class NaivePatternSearch {

    static List<Integer> search(String pat, String txt) {
        if (pat == null || txt == null || pat.isEmpty() || pat.length() > txt.length()) {
            return new ArrayList<>();
        }

        int m = pat.length();
        int n = txt.length();
        List<Integer> occurrences = new ArrayList<>();

        for (int i = 0; i <= n - m; i++) {
            int j;
            for (j = 0; j < m; j++) {
                if (txt.charAt(i + j) != pat.charAt(j)) {
                    break;
                }
            }
            if (j == m) {
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
