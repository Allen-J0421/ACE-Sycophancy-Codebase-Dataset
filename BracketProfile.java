import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class BracketProfile {
    private final Map<Character, Character> openingToClosing;
    private final Set<Character> closingBrackets;

    private BracketProfile(Map<Character, Character> openingToClosing, Set<Character> closingBrackets) {
        this.openingToClosing = openingToClosing;
        this.closingBrackets = closingBrackets;
    }

    public static BracketProfile standard() {
        return of(Map.of(
            '(', ')',
            '{', '}',
            '[', ']'
        ));
    }

    public static BracketProfile of(Map<Character, Character> openingToClosing) {
        Objects.requireNonNull(openingToClosing, "openingToClosing");
        Map<Character, Character> normalizedPairs = Map.copyOf(openingToClosing);
        Set<Character> closingBrackets = Set.copyOf(normalizedPairs.values());
        validatePairs(normalizedPairs, closingBrackets);
        return new BracketProfile(normalizedPairs, closingBrackets);
    }

    Character expectedClosingFor(char candidate) {
        return openingToClosing.get(candidate);
    }

    boolean isClosingBracket(char candidate) {
        return closingBrackets.contains(candidate);
    }

    private static void validatePairs(
        Map<Character, Character> openingToClosing,
        Set<Character> closingBrackets
    ) {
        if (closingBrackets.size() != openingToClosing.size()) {
            throw new IllegalArgumentException("closing brackets must be unique");
        }

        Set<Character> overlappingBrackets = new HashSet<>(openingToClosing.keySet());
        overlappingBrackets.retainAll(closingBrackets);
        if (!overlappingBrackets.isEmpty()) {
            throw new IllegalArgumentException("opening and closing brackets must be distinct");
        }
    }
}
