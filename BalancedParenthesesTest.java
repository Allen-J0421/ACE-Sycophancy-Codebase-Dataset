import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
