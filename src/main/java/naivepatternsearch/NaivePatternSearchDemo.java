package naivepatternsearch;

import java.util.List;

public final class NaivePatternSearchDemo {

    private NaivePatternSearchDemo() {
        // Demo entry point.
    }

    public static void main(String[] args) {
        String[] input = parseArguments(args);
        if (input == null) {
            return;
        }

        printMatches(NaivePatternSearch.search(input[0], input[1]));
    }

    private static String[] parseArguments(String[] args) {
        if (args.length == 0) {
            return new String[] {"aaba", "aabaacaadaabaaba"};
        }
        if (args.length == 2) {
            return args;
        }

        System.err.println("Usage: NaivePatternSearchDemo [pattern text]");
        return null;
    }

    private static void printMatches(List<Integer> matches) {
        for (int index : matches) {
            System.out.print(index + " ");
        }
        System.out.println();
    }
}
