package rabinkarp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RabinKarpMatcher {

    public static final int DEFAULT_RADIX = 256;
    public static final int DEFAULT_MODULUS = 101;

    private final int radix;
    private final int modulus;

    public RabinKarpMatcher(int radix, int modulus) {
        if (radix <= 0) {
            throw new IllegalArgumentException("radix must be positive");
        }
        if (modulus <= 1) {
            throw new IllegalArgumentException("modulus must be greater than 1");
        }
        this.radix = radix;
        this.modulus = modulus;
    }

    public List<Integer> search(CharSequence pattern, CharSequence text) {
        return search(RabinKarpPattern.compile(pattern, radix, modulus), text);
    }

    List<Integer> search(RabinKarpPattern compiledPattern, CharSequence text) {
        Objects.requireNonNull(compiledPattern, "compiledPattern");
        Objects.requireNonNull(text, "text");

        int patternLength = compiledPattern.length();
        int textLength = text.length();

        if (patternLength == 0) {
            return allMatchPositions(textLength);
        }
        if (patternLength > textLength) {
            return List.of();
        }

        int patternHash = compiledPattern.hash();
        int windowHash = hashOf(text, patternLength);
        int highOrderFactor = compiledPattern.highOrderFactor();
        int lastStart = textLength - patternLength;
        List<Integer> matches = new ArrayList<>();

        for (int offset = 0; offset <= lastStart; offset++) {
            if (patternHash == windowHash && matchesAt(text, compiledPattern.pattern(), offset)) {
                matches.add(offset);
            }

            if (offset < lastStart) {
                windowHash = rollHash(
                        windowHash,
                        text.charAt(offset),
                        text.charAt(offset + patternLength),
                        highOrderFactor);
            }
        }

        return List.copyOf(matches);
    }

    private List<Integer> allMatchPositions(int textLength) {
        List<Integer> matches = new ArrayList<>(textLength + 1);
        for (int i = 0; i <= textLength; i++) {
            matches.add(i);
        }
        return List.copyOf(matches);
    }

    private int hashOf(CharSequence sequence, int length) {
        int hash = 0;
        for (int i = 0; i < length; i++) {
            hash = (radix * hash + sequence.charAt(i)) % modulus;
        }
        return hash;
    }

    private boolean matchesAt(CharSequence text, CharSequence pattern, int offset) {
        for (int i = 0; i < pattern.length(); i++) {
            if (text.charAt(offset + i) != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private int rollHash(int currentHash, char outgoing, char incoming, int highOrderFactor) {
        int nextHash = radix * (currentHash - outgoing * highOrderFactor) + incoming;
        nextHash %= modulus;
        if (nextHash < 0) {
            nextHash += modulus;
        }
        return nextHash;
    }
}
