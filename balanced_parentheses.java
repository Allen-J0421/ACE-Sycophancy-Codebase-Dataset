import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BalancedParentheses {

    static class BracketMatcher {
        private final Map<Character, Character> closerToOpener;
        private final Set<Character> openers;

        BracketMatcher(Map<Character, Character> closerToOpener) {
            this.closerToOpener = closerToOpener;
            this.openers = new HashSet<>(closerToOpener.values());
        }

        boolean isOpener(char c) {
            return openers.contains(c);
        }

        boolean isCloser(char c) {
            return closerToOpener.containsKey(c);
        }

        boolean matches(char opener, char closer) {
            return opener == closerToOpener.getOrDefault(closer, '\0');
        }
    }

    private static final Map<Character, Character> DEFAULT_PAIRS = Map.of(
        ')', '(',
        '}', '{',
        ']', '['
    );

    public static boolean isBalanced(String s) {
        return isBalanced(s, DEFAULT_PAIRS);
    }

    static class ValidationContext {
        private final BracketMatcher matcher;
        private final Deque<Character> stack = new ArrayDeque<>();

        ValidationContext(BracketMatcher matcher) {
            this.matcher = matcher;
        }

        boolean process(char c) {
            if (matcher.isOpener(c)) { stack.push(c); return true; }
            if (matcher.isCloser(c)) {
                if (stack.isEmpty() || !matcher.matches(stack.peek(), c)) return false;
                stack.pop();
            }
            return true;
        }

        boolean isComplete() {
            return stack.isEmpty();
        }
    }

    public static boolean isBalanced(String s, Map<Character, Character> pairs) {
        ValidationContext ctx = new ValidationContext(new BracketMatcher(pairs));
        return s.chars()
            .mapToObj(c -> (char) c)
            .allMatch(ctx::process)
            && ctx.isComplete();
    }

    public static void main(String[] args) {
        String s = "[()()]{}";
        System.out.println((isBalanced(s) ? "true" : "false"));
    }
}
