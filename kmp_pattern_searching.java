import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class KMPSearch {

    private static int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else if (len != 0) {
                len = lps[len - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }
        return lps;
    }

    static List<Integer> search(String pattern, String text) {
        if (pattern == null || text == null || pattern.isEmpty() || pattern.length() > text.length()) {
            return Collections.emptyList();
        }
        int n = text.length();
        int m = pattern.length();
        int[] lps = computeLPS(pattern);
        List<Integer> result = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    result.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j != 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String text = "aabaacaadaabaaba";
        String pattern = "aaba";
        List<Integer> result = search(pattern, text);
        for (int index : result) {
            System.out.print(index + " ");
        }
        System.out.println();
    }
}
