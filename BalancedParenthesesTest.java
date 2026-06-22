import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.CharBuffer;
import java.util.Map;

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
        assertTrue(BalancedParentheses.isBalanced(CharBuffer.wrap("[{}()]")));
    }

    @Test
    void supportsCustomBracketProfiles() {
        BracketProfile angleAndPipeProfile = BracketProfile.of(
            Map.of('<', '>', '|', '!')
        );

        assertTrue(BalancedParentheses.isBalanced("<|!>", angleAndPipeProfile));
        assertFalse(BalancedParentheses.isBalanced("<|>", angleAndPipeProfile));
    }

    @Test
    void rejectsInvalidBracketProfiles() {
        assertThrows(
            IllegalArgumentException.class,
            () -> BracketProfile.of(Map.of('(', ')', '[', ')'))
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> BracketProfile.of(Map.of('(', ')', ')', ']'))
        );
    }

    @Test
    void rejectsNullInput() {
        assertThrows(NullPointerException.class, () -> BalancedParentheses.isBalanced(null));
    }
}
