import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class BracketProfile {
    private static final BracketProfile STANDARD = of(Map.of('(', ')', '{', '}', '[', ']'));

    private final Map<Character, BracketInfo> bracketInfos;

    private BracketProfile(Map<Character, BracketInfo> bracketInfos) {
        this.bracketInfos = bracketInfos;
    }

    public static BracketProfile standard() {
        return STANDARD;
    }

    public static BracketProfile of(Map<Character, Character> openingToClosing) {
        Objects.requireNonNull(openingToClosing, "openingToClosing");
        return buildProfile(Map.copyOf(openingToClosing));
    }

    BracketInfo bracketFor(char c) {
        return bracketInfos.get(Character.valueOf(c));
    }

    private static BracketProfile buildProfile(Map<Character, Character> openingToClosing) {
        Map<Character, BracketInfo> infos = new HashMap<>();
        Set<Character> usedCharacters = new HashSet<>();
        Set<Character> closingBrackets = new HashSet<>();
        for (Map.Entry<Character, Character> entry : openingToClosing.entrySet()) {
            char opening = entry.getKey().charValue();
            char closing = entry.getValue().charValue();

            validatePair(opening, closing, usedCharacters, closingBrackets);
            registerPair(infos, usedCharacters, closingBrackets, opening, closing);
        }
        return new BracketProfile(Map.copyOf(infos));
    }

    private static void validatePair(
        char opening,
        char closing,
        Set<Character> usedCharacters,
        Set<Character> closingBrackets
    ) {
        if (closingBrackets.contains(closing)) {
            throw new IllegalArgumentException("closing brackets must be unique");
        }
        if (opening == closing
            || usedCharacters.contains(opening)
            || usedCharacters.contains(closing)) {
            throw new IllegalArgumentException("opening and closing brackets must be distinct");
        }
    }

    private static void registerPair(
        Map<Character, BracketInfo> infos,
        Set<Character> usedCharacters,
        Set<Character> closingBrackets,
        char opening,
        char closing
    ) {
        usedCharacters.add(opening);
        usedCharacters.add(closing);
        closingBrackets.add(closing);
        infos.put(Character.valueOf(opening), BracketInfo.opening(opening, closing));
        infos.put(Character.valueOf(closing), BracketInfo.closing(closing));
    }

    record BracketInfo(char character, char expectedClosing, boolean opening) {
        static BracketInfo opening(char character, char expectedClosing) {
            return new BracketInfo(character, expectedClosing, true);
        }

        static BracketInfo closing(char character) {
            return new BracketInfo(character, character, false);
        }

        boolean isOpening() {
            return opening;
        }
    }
}
