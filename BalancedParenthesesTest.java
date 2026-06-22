import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BalancedParenthesesTest {
    @Test
    void returnsTrueForBalancedBrackets() {
        assertTrue(BalancedParentheses.isBalanced("[()()]{}"));
    }

    @Test
    void returnsFalseForMismatchedBrackets() {
        assertFalse(BalancedParentheses.isBalanced("[(])"));
    }

    @Test
    void returnsFalseForUnclosedBrackets() {
        assertFalse(BalancedParentheses.isBalanced("(()"));
    }

    @Test
    void ignoresNonBracketCharacters() {
        assertTrue(BalancedParentheses.isBalanced("(a + b) * [c]"));
    }
}
