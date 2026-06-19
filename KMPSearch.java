import java.util.ArrayList;
import java.util.List;

public final class KMPSearch {

    private KMPSearch() {
        // Utility class.
    }

    private static int[] buildLps(String pattern) {
        int[] lps = new int[pattern.length()];
        int prefixLength = 0;

        for (int i = 1; i < pattern.length();) {
            if (pattern.charAt(i) == pattern.charAt(prefixLength)) {
                lps[i++] = ++prefixLength;
            } else if (prefixLength != 0) {
                prefixLength = lps[prefixLength - 1];
            } else {
                lps[i++] = 0;
            }
        }

        return lps;
    }

    static ArrayList<Integer> search(String pat, String txt) {
        if (pat == null || txt == null) {
            throw new IllegalArgumentException("Pattern and text must not be null.");
        }

        ArrayList<Integer> matches = new ArrayList<>();
        if (pat.isEmpty() || txt.isEmpty() || pat.length() > txt.length()) {
            return matches;
        }

        int[] lps = buildLps(pat);
        int textIndex = 0;
        int patternIndex = 0;

        while (textIndex < txt.length()) {
            if (txt.charAt(textIndex) == pat.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;

                if (patternIndex == pat.length()) {
                    matches.add(textIndex - patternIndex);
                    patternIndex = lps[patternIndex - 1];
                }
            } else if (patternIndex != 0) {
                patternIndex = lps[patternIndex - 1];
            } else {
                textIndex++;
            }
        }

        return matches;
    }

    private static void printMatches(List<Integer> matches) {
        for (int i = 0; i < matches.size(); i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(matches.get(i));
        }
        System.out.println();
    }

    public static void main(String[] args) {
        String txt = "aabaacaadaabaaba";
        String pat = "aaba";
        printMatches(search(pat, txt));
    }
}
