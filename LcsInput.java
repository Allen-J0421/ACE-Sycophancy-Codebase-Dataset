import java.util.Objects;

final class LcsInput {
    private final Integer knownResult;
    private final NormalizedSequences normalizedSequences;

    private LcsInput(Integer knownResult, NormalizedSequences normalizedSequences) {
        this.knownResult = knownResult;
        this.normalizedSequences = normalizedSequences;
    }

    static LcsInput from(String first, String second) {
        Objects.requireNonNull(first, "first must not be null");
        Objects.requireNonNull(second, "second must not be null");

        if (first.isEmpty() || second.isEmpty()) {
            return new LcsInput(0, null);
        }

        if (first.equals(second)) {
            return new LcsInput(first.length(), null);
        }

        return new LcsInput(null, normalize(first, second));
    }

    private static NormalizedSequences normalize(String first, String second) {
        char[] firstChars = first.toCharArray();
        char[] secondChars = second.toCharArray();

        if (firstChars.length >= secondChars.length) {
            return new NormalizedSequences(firstChars, secondChars);
        }

        return new NormalizedSequences(secondChars, firstChars);
    }

    boolean hasKnownResult() {
        return knownResult != null;
    }

    int knownResult() {
        return knownResult;
    }

    NormalizedSequences normalizedSequences() {
        return normalizedSequences;
    }
}
