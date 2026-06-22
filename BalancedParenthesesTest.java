import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class BalancedParenthesesTest {
    @ParameterizedTest
    @ValueSource(strings = {
        "[()()]{}",
        "(a + b) * [c]",
        "",
        "no brackets here"
    })
    void returnsTrueForBalancedInputs(String input) {
        assertTrue(BalancedParentheses.isBalanced(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "[(])",
        "(()",
        "())",
        "}{"
    })
    void returnsFalseForUnbalancedInputs(String input) {
        assertFalse(BalancedParentheses.isBalanced(input));
    }
}
