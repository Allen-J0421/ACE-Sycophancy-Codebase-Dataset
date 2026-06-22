import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class BracketProfile {
    private static final BracketProfile STANDARD = of(Map.of('(', ')', '{', '}', '[', ']'));

    private final Map<Character, BracketToken> bracketTokens;

    private BracketProfile(Map<Character, BracketToken> bracketTokens) {
        this.bracketTokens = bracketTokens;
    }

    public static BracketProfile standard() {
        return STANDARD;
    }

    public static BracketProfile of(Map<Character, Character> openingToClosing) {
        Objects.requireNonNull(openingToClosing, "openingToClosing");
        return new BracketProfile(buildBracketTokens(Map.copyOf(openingToClosing)));
    }

    BracketToken tokenFor(char c) {
        return bracketTokens.get(Character.valueOf(c));
    }

    boolean containsBracket(char c) {
        return bracketTokens.containsKey(Character.valueOf(c));
    }

    boolean isOpeningBracket(char c) {
        BracketToken token = tokenFor(c);
        return token != null && token.isOpening();
    }

    char expectedClosingOf(char c) {
        BracketToken token = tokenFor(c);
        if (token == null) {
            throw new IllegalArgumentException("unknown opening bracket: " + c);
        }
        return token.expectedClosing();
    }

    private static Map<Character, BracketToken> buildBracketTokens(
        Map<Character, Character> openingToClosing
    ) {
        Map<Character, BracketToken> tokens = new HashMap<>();
        Set<Character> openingBrackets = new HashSet<>();
        Set<Character> closingBrackets = new HashSet<>();
        for (Map.Entry<Character, Character> entry : openingToClosing.entrySet()) {
            char opening = entry.getKey().charValue();
            char closing = entry.getValue().charValue();

            if (!closingBrackets.add(closing)) {
                throw new IllegalArgumentException("closing brackets must be unique");
            }
            if (opening == closing
                || openingBrackets.contains(closing)
                || closingBrackets.contains(opening)) {
                throw new IllegalArgumentException("opening and closing brackets must be distinct");
            }

            openingBrackets.add(opening);
            tokens.put(Character.valueOf(opening), BracketToken.opening(opening, closing));
            tokens.put(Character.valueOf(closing), BracketToken.closing(closing));
        }
        return Map.copyOf(tokens);
    }
}
