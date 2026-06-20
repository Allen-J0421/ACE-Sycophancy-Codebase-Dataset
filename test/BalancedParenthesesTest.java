import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit 5 test suite for {@link BalancedParentheses#isBalanced(String)}.
 *
 * <p>Compile and run with {@code ./run-tests.sh} (no build tool required).
 */
@DisplayName("BalancedParentheses.isBalanced")
class BalancedParenthesesTest {

    @Nested
    @DisplayName("Balanced inputs")
    class Balanced {

        @ParameterizedTest(name = "\"{0}\" is balanced")
        @EmptySource
        @ValueSource(strings = {
            "()",
            "{}",
            "[]",
            "()[]{}",
            "([{}])",
            "[()()]{}",
            "{[()()]}",
            "((((()))))",
        })
        @DisplayName("well-formed bracket strings")
        void recognizesBalanced(String input) {
            assertTrue(BalancedParentheses.isBalanced(input));
        }

        @Test
        @DisplayName("deeply nested mixed brackets")
        void deeplyNested() {
            assertTrue(BalancedParentheses.isBalanced("[{([{([])}])}]"));
        }
    }

    @Nested
    @DisplayName("Unbalanced inputs")
    class Unbalanced {

        @ParameterizedTest(name = "\"{0}\" is not balanced")
        @ValueSource(strings = {
            "(",            // unclosed opener
            ")",            // stray closer
            "((",           // unclosed openers
            "))",           // stray closers
            "(]",           // mismatched pair
            "([)]",         // crossed nesting
            "{[}]",         // crossed nesting
            "(()",          // one opener left open
            "())",          // one extra closer
            "]",            // closer with empty stack
        })
        @DisplayName("malformed bracket strings")
        void rejectsUnbalanced(String input) {
            assertFalse(BalancedParentheses.isBalanced(input));
        }

        @Test
        @DisplayName("opener with wrong closer at the very end")
        void wrongClosingType() {
            assertFalse(BalancedParentheses.isBalanced("([]{}"));
        }
    }

    @Nested
    @DisplayName("Non-bracket characters")
    class NonBracketCharacters {

        @Test
        @DisplayName("are ignored, leaving an empty string balanced")
        void plainTextIsBalanced() {
            assertTrue(BalancedParentheses.isBalanced("hello, world! 123"));
        }

        @Test
        @DisplayName("are ignored but brackets are still validated")
        void interleavedWithText() {
            assertTrue(BalancedParentheses.isBalanced("a(b)c[d]e{f}g"));
            assertFalse(BalancedParentheses.isBalanced("a(b]c"));
        }
    }

    @Nested
    @DisplayName("Custom bracket pairs")
    class CustomPairs {

        /** Angle brackets only; the standard brackets are now just plain text. */
        private static final Map<Character, Character> ANGLES = Map.of('>', '<');

        @Test
        @DisplayName("only the supplied pairs are treated as brackets")
        void honorsCustomDefinitions() {
            assertTrue(BalancedParentheses.isBalanced("<<a>>", ANGLES));
            assertFalse(BalancedParentheses.isBalanced("<<a>", ANGLES));
            // Standard brackets are ignored because they are not in the map.
            assertTrue(BalancedParentheses.isBalanced("(unmatched", ANGLES));
        }

        @Test
        @DisplayName("supports multiple custom pairs together")
        void multiplePairs() {
            Map<Character, Character> pairs = Map.of('>', '<', ')', '(');
            assertTrue(BalancedParentheses.isBalanced("<(a)>", pairs));
            assertFalse(BalancedParentheses.isBalanced("<(>)", pairs));
        }

        @Test
        @DisplayName("handles non-ASCII brackets (code points above the Character cache)")
        void nonAsciiBrackets() {
            // U+00AB/U+00BB guillemets and U+300C/U+300D CJK corner brackets are all
            // above 127, so a boxed-object '!=' comparison would be unreliable here.
            Map<Character, Character> pairs = Map.of('»', '«', '」', '「');
            assertTrue(BalancedParentheses.isBalanced("«「x」»", pairs));
            assertFalse(BalancedParentheses.isBalanced("«「x»」", pairs));
        }

        @Test
        @DisplayName("an empty pair map leaves every string balanced")
        void emptyPairMap() {
            assertTrue(BalancedParentheses.isBalanced("([{<>}])", Map.of()));
        }
    }
}
