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
        Set<Character> closingBrackets = new HashSet<>();
        for (Map.Entry<Character, Character> entry : openingToClosing.entrySet()) {
            char opening = entry.getKey().charValue();
            char closing = entry.getValue().charValue();

            if (!closingBrackets.add(closing)) {
                throw new IllegalArgumentException("closing brackets must be unique");
            }
            if (opening == closing
                || infos.containsKey(Character.valueOf(closing))
                || closingBrackets.contains(opening)) {
                throw new IllegalArgumentException(
                    "opening and closing brackets must be distinct"
                );
            }

            BracketInfo openingInfo = BracketInfo.opening(opening, closing);
            BracketInfo closingInfo = BracketInfo.closing(closing);
            infos.put(Character.valueOf(opening), openingInfo);
            infos.put(Character.valueOf(closing), closingInfo);
        }
        return new BracketProfile(Map.copyOf(infos));
    }

    static final class BracketInfo {
        private final char character;
        private final char expectedClosing;
        private final boolean opening;

        private BracketInfo(char character, char expectedClosing, boolean opening) {
            this.character = character;
            this.expectedClosing = expectedClosing;
            this.opening = opening;
        }

        static BracketInfo opening(char character, char expectedClosing) {
            return new BracketInfo(character, expectedClosing, true);
        }

        static BracketInfo closing(char character) {
            return new BracketInfo(character, character, false);
        }

        char character() {
            return character;
        }

        char expectedClosing() {
            return expectedClosing;
        }

        boolean isOpening() {
            return opening;
        }
    }
}
