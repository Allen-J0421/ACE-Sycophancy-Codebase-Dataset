import java.util.ArrayList;

public final class NaivePatternSearch {

    private NaivePatternSearch() {
        // Utility class.
    }

    public static ArrayList<Integer> search(String pattern, String text) {
        int patternLength = pattern.length();
        int textLength = text.length();
        ArrayList<Integer> matches = new ArrayList<>();

        for (int start = 0; start <= textLength - patternLength; start++) {
            if (matchesAt(pattern, text, start)) {
                matches.add(start);
            }
        }

        return matches;
    }

    private static boolean matchesAt(String pattern, String text, int start) {
        for (int offset = 0; offset < pattern.length(); offset++) {
            if (text.charAt(start + offset) != pattern.charAt(offset)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String text = "aabaacaadaabaaba";
        String pattern = "aaba";

        ArrayList<Integer> matches = search(pattern, text);
        for (int index : matches) {
            System.out.print(index + " ");
        }
    }
}
