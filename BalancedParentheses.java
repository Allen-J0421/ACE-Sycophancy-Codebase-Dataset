import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BalancedParentheses {
    private static final String DEFAULT_SAMPLE = "[()()]{}";
    private static final BracketProfile DEFAULT_BRACKETS = BracketProfile.standard();

    private BalancedParentheses() {}

    public static boolean isBalanced(CharSequence input) {
        Objects.requireNonNull(input, "input");
        return new BalanceChecker(DEFAULT_BRACKETS).check(input);
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

    private static final class BracketProfile {
        private final Map<Character, Character> openingToClosing;
        private final Set<Character> closingBrackets;

        private BracketProfile(Map<Character, Character> openingToClosing, Set<Character> closingBrackets) {
            this.openingToClosing = openingToClosing;
            this.closingBrackets = closingBrackets;
        }

        private static BracketProfile standard() {
            Map<Character, Character> openingToClosing = Map.of(
                '(', ')',
                '{', '}',
                '[', ']'
            );
            return new BracketProfile(openingToClosing, Set.copyOf(openingToClosing.values()));
        }

        private Character expectedClosingFor(char candidate) {
            return openingToClosing.get(candidate);
        }

        private boolean isClosingBracket(char candidate) {
            return closingBrackets.contains(candidate);
        }
    }
}
