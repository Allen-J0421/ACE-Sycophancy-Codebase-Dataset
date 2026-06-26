package naivepatternsearch;

import java.util.List;

public final class NaivePatternSearchDemo {

    private NaivePatternSearchDemo() {
        // Demo entry point.
    }

    public static void main(String[] args) {
        String pattern;
        String text;

        if (args.length == 0) {
            pattern = "aaba";
            text = "aabaacaadaabaaba";
        } else if (args.length == 2) {
            pattern = args[0];
            text = args[1];
        } else {
            System.err.println("Usage: NaivePatternSearchDemo [pattern text]");
            return;
        }

        List<Integer> matches = PatternSearchers.naive().search(pattern, text);
        for (int index : matches) {
            System.out.print(index + " ");
        }
        System.out.println();
    }
}
