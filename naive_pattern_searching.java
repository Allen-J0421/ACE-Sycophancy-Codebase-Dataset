import java.util.ArrayList;
import java.util.List;

public class NaivePatternSearch {

    private static boolean matchesAt(String text, String pattern, int startIndex) {
        for (int i = 0; i < pattern.length(); i++) {
            if (text.charAt(startIndex + i) != pattern.charAt(i))
                return false;
        }
        return true;
    }

    static List<Integer> search(String pattern, String text) {
        int patternLen = pattern.length();
        int textLen = text.length();
        List<Integer> matches = new ArrayList<>();

        for (int i = 0; i <= textLen - patternLen; i++) {
            if (matchesAt(text, pattern, i))
                matches.add(i);
        }

        return matches;
    }

    public static void main(String[] args)
    {
        String txt = "aabaacaadaabaaba";
        String pat = "aaba";

        List<Integer> res = search(pat, txt);

        for (int it : res)
        {
            System.out.print(it + " ");
        }
    }
}
