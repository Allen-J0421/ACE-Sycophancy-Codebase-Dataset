package rabinkarp;

import java.util.List;

public interface StringMatcher {
    List<Integer> search(CharSequence pattern, CharSequence text);
}
