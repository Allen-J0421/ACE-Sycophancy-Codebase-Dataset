import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BalancedParentheses {
    private static final String DEFAULT_SAMPLE = "[()()]{}";
    private static final BracketProfile DEFAULT_BRACKETS = BracketProfile.standard();

    private BalancedParentheses() {}

    public static boolean isBalanced(CharSequence input) {
        return isBalanced(input, DEFAULT_BRACKETS);
    }

    public static boolean isBalanced(CharSequence input, BracketProfile bracketProfile) {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(bracketProfile, "bracketProfile");
        return new BalanceChecker(bracketProfile).check(input);
    }

    public static void main(String[] args) {
        String s = args.length > 0 ? args[0] : DEFAULT_SAMPLE;
        System.out.println((isBalanced(s) ? "true" : "false"));
    }

    private static final class BalanceChecker {
        private final BracketProfile bracketProfile;
        private final Deque<Character> expectedClosings = new ArrayDeque<>();

        private BalanceChecker(BracketProfile bracketProfile) {
            this.bracketProfile = bracketProfile;
        }

        private boolean check(CharSequence input) {
            for (int i = 0; i < input.length(); i++) {
                if (!accept(input.charAt(i))) {
                    return false;
                }
            }

            return expectedClosings.isEmpty();
        }

        private boolean accept(char candidate) {
            Character expectedClosing = bracketProfile.expectedClosingFor(candidate);
            if (expectedClosing != null) {
                expectedClosings.push(expectedClosing);
                return true;
            }

            if (!bracketProfile.isClosingBracket(candidate)) {
                return true;
            }

            return !expectedClosings.isEmpty() && expectedClosings.pop() == candidate;
        }
    }

    public static final class BracketProfile {
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

        private Character expectedClosingFor(char candidate) {
            return openingToClosing.get(candidate);
        }

        private boolean isClosingBracket(char candidate) {
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
}
