package rabinkarp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class RabinKarpTest {

    @Test
    void findsBasicMatches() {
        assertEquals(List.of(0, 8), RabinKarp.search("geeks", "geeksforgeeks"));
    }

    @Test
    void returnsAllPositionsForEmptyPattern() {
        assertEquals(List.of(0, 1, 2, 3), RabinKarp.search("", "abc"));
    }

    @Test
    void returnsEmptyListWhenPatternIsLongerThanText() {
        assertEquals(List.of(), RabinKarp.search("abcd", "abc"));
    }

    @Test
    void returnsEmptyListWhenNoMatchExists() {
        assertEquals(List.of(), RabinKarp.search("xyz", "abcdef"));
    }

    @Test
    void acceptsAnyCharSequence() {
        assertEquals(
                List.of(0, 8),
                RabinKarp.search(new StringBuilder("geeks"), new StringBuilder("geeksforgeeks")));
    }

    @Test
    void supportsCustomMatcherConfiguration() {
        assertEquals(
                List.of(0, 8),
                new RabinKarpMatcher(256, 101).search("geeks", "geeksforgeeks"));
    }
}
