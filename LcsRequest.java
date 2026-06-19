import java.util.Objects;

final class LcsRequest {
    private final char[] longer;
    private final char[] shorter;
    private final Integer knownResult;

    private LcsRequest(char[] longer, char[] shorter, Integer knownResult) {
        this.longer = longer;
        this.shorter = shorter;
        this.knownResult = knownResult;
    }

    static LcsRequest from(String first, String second) {
        Objects.requireNonNull(first, "first must not be null");
        Objects.requireNonNull(second, "second must not be null");

        if (first.isEmpty() || second.isEmpty()) {
            return new LcsRequest(first.toCharArray(), second.toCharArray(), 0);
        }

        if (first.equals(second)) {
            return new LcsRequest(first.toCharArray(), second.toCharArray(), first.length());
        }

        char[] firstChars = first.toCharArray();
        char[] secondChars = second.toCharArray();

        if (firstChars.length >= secondChars.length) {
            return new LcsRequest(firstChars, secondChars, null);
        }

        return new LcsRequest(secondChars, firstChars, null);
    }

    boolean hasKnownResult() {
        return knownResult != null;
    }

    int knownResult() {
        return knownResult;
    }

    char[] longer() {
        return longer;
    }

    char[] shorter() {
        return shorter;
    }
}
