import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class BracketProfile {
    private static final BracketProfile STANDARD = of(Map.of('(', ')', '{', '}', '[', ']'));

    private final Map<Character, Character> openingToClosing;
    private final Set<Character> closingBrackets;

    private BracketProfile(
        Map<Character, Character> openingToClosing,
        Set<Character> closingBrackets
    ) {
        this.openingToClosing = openingToClosing;
        this.closingBrackets = closingBrackets;
    }

    public static BracketProfile standard() {
        return STANDARD;
    }

    public static BracketProfile of(Map<Character, Character> openingToClosing) {
        Objects.requireNonNull(openingToClosing, "openingToClosing");
        return buildProfile(Map.copyOf(openingToClosing));
    }

    boolean isBracket(char c) {
        return openingToClosing.containsKey(Character.valueOf(c))
            || closingBrackets.contains(Character.valueOf(c));
    }

    boolean isOpeningBracket(char c) {
        return openingToClosing.containsKey(Character.valueOf(c));
    }

    char expectedClosingOf(char c) {
        Character closing = openingToClosing.get(Character.valueOf(c));
        if (closing == null) {
            throw new IllegalArgumentException("unknown opening bracket: " + c);
        }
        return closing.charValue();
    }

    private static BracketProfile buildProfile(Map<Character, Character> openingToClosing) {
        Map<Character, Character> openings = new HashMap<>();
        Set<Character> openingBrackets = new HashSet<>();
        Set<Character> closingBrackets = new HashSet<>();
        for (Map.Entry<Character, Character> entry : openingToClosing.entrySet()) {
            char opening = entry.getKey().charValue();
            char closing = entry.getValue().charValue();

            if (opening == closing
                || openingBrackets.contains(closing)
                || closingBrackets.contains(opening)) {
                throw new IllegalArgumentException(
                    "opening and closing brackets must be distinct"
                );
            }
            if (!closingBrackets.add(closing)) {
                throw new IllegalArgumentException("closing brackets must be unique");
            }

            openingBrackets.add(opening);
            openings.put(Character.valueOf(opening), Character.valueOf(closing));
        }
        return new BracketProfile(Map.copyOf(openings), Set.copyOf(closingBrackets));
    }
}
