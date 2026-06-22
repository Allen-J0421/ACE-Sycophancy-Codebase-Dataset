import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class BalancedParenthesesTest {
    @ParameterizedTest
    @CsvSource({
        "'[()()]{}', true",
        "'(a + b) * [c]', true",
        "'', true",
        "'no brackets here', true",
        "'[(])', false",
        "'(()', false",
        "'())', false",
        "'}{', false"
    })
    void returnsExpectedBalanceResult(String input, boolean expectedBalanced) {
        assertEquals(expectedBalanced, BalancedParentheses.isBalanced(input));
    }

    @Test
    void supportsNonStringCharSequences() {
        assertTrue(BalancedParentheses.isBalanced(new StringBuilder("{[()()]}")));
    }

    @Test
    void rejectsNullInput() {
        assertThrows(NullPointerException.class, () -> BalancedParentheses.isBalanced(null));
    }
}
