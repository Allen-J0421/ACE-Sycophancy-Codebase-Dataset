package com.example.kmp.demo;

import com.example.kmp.KmpMatchResult;
import com.example.kmp.KmpMatcher;
import com.example.kmp.KmpPattern;
import com.example.kmp.KmpSearch;

public final class KmpSearchDemo {
    private static final String DEFAULT_TEXT = "aabaacaadaabaaba";
    private static final String DEFAULT_PATTERN = "aaba";

    private KmpSearchDemo() {
    }

    public static void main(String[] args) {
        String text = args.length > 0 ? args[0] : DEFAULT_TEXT;
        String pattern = args.length > 1 ? args[1] : DEFAULT_PATTERN;

        KmpPattern compiledPattern = KmpSearch.compile(pattern);
        KmpMatcher matcher = compiledPattern.matcher(text);
        KmpMatchResult result = matcher.analyze();

        System.out.println("Matches: " + result.matchIndices());
        System.out.println("Count: " + result.count());
        System.out.println("First: " + formatOptional(result.firstMatch()));
        System.out.println("Last: " + formatOptional(result.lastMatch()));
        System.out.println("Pattern: " + result.pattern());
        System.out.println("Text: " + result.text());
        System.out.println("Contains: " + result.hasMatches());

        System.out.print("Iterated: ");
        for (int matchIndex : result) {
            System.out.print(matchIndex + " ");
        }
        if (!matcher.contains()) {
            System.out.print("none");
        }
        System.out.println();
    }

    private static String formatOptional(java.util.OptionalInt value) {
        return value.isPresent() ? Integer.toString(value.getAsInt()) : "none";
    }
}
