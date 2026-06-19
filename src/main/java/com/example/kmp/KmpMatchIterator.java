package com.example.kmp;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

/**
 * One-shot iterator over zero-based KMP match indices for a specific text input.
 */
public final class KmpMatchIterator implements PrimitiveIterator.OfInt {
    private final String pattern;
    private final int[] longestPrefixSuffix;
    private final CharSequence text;

    private int textIndex;
    private int patternIndex;
    private int nextMatchIndex;
    private boolean nextMatchReady;
    private boolean exhausted;

    KmpMatchIterator(String pattern, int[] longestPrefixSuffix, CharSequence text) {
        this.pattern = pattern;
        this.longestPrefixSuffix = longestPrefixSuffix;
        this.text = text;
        this.exhausted = pattern.length() > text.length();
    }

    @Override
    public boolean hasNext() {
        if (nextMatchReady) {
            return true;
        }

        if (exhausted) {
            return false;
        }

        while (textIndex < text.length()) {
            if (text.charAt(textIndex) == pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;

                if (patternIndex == pattern.length()) {
                    nextMatchIndex = textIndex - patternIndex;
                    patternIndex = longestPrefixSuffix[patternIndex - 1];
                    nextMatchReady = true;
                    return true;
                }
                continue;
            }

            if (patternIndex > 0) {
                patternIndex = longestPrefixSuffix[patternIndex - 1];
            } else {
                textIndex++;
            }
        }

        exhausted = true;
        return false;
    }

    @Override
    public int nextInt() {
        if (!hasNext()) {
            throw new NoSuchElementException("No remaining matches");
        }

        nextMatchReady = false;
        return nextMatchIndex;
    }
}
