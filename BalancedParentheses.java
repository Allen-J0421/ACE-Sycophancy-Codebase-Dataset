import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;

public class BalancedParentheses {
    // Default bracket set, as closing bracket -> matching opening bracket.
    private static final Map<Character, Character> DEFAULT_PAIRS = Map.of(
        ')', '(',
        '}', '{',
        ']', '['
    );

    /**
     * Checks whether the standard brackets {@code () [] {}} are balanced in
     * {@code s}. Any other character is ignored.
     */
    public static boolean isBalanced(String s) {
        return isBalanced(s, DEFAULT_PAIRS);
    }

    /**
     * Checks whether the brackets defined by {@code pairs} are balanced in {@code s}.
     *
     * <p>Each entry maps a <em>closing</em> bracket to its matching <em>opening</em>
     * bracket — the same direction as the default set. For example, to support
     * angle and guillemet brackets in addition to nothing else:
     * <pre>{@code
     * Map<Character, Character> pairs = Map.of('>', '<', '»', '«');
     * isBalanced("<<a>>", pairs);   // characters not in the map are ignored
     * }</pre>
     *
     * <p>Opening and closing brackets must be distinct characters; a character that
     * is both an opener and a closer is always treated as an opener.
     *
     * @param s     the text to scan
     * @param pairs closing-to-opening bracket definitions
     * @return {@code true} if every closer matches the most recent unmatched opener
     *         and no opener is left unclosed
     */
    public static boolean isBalanced(String s, Map<Character, Character> pairs) {
        Set<Character> openers = Set.copyOf(pairs.values());
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (openers.contains(c)) {
                stack.push(c);
            } else if (pairs.containsKey(c)) {
                // Compare by value: custom pairs may use non-ASCII brackets, whose
                // boxed Character values are not interned, so '!=' on objects would
                // not be reliable.
                if (stack.isEmpty() || stack.pop().charValue() != pairs.get(c).charValue()) {
                    return false;
                }
            }
            // Any other character is ignored.
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        String s = "[()()]{}";
        System.out.println(isBalanced(s));
    }
}
