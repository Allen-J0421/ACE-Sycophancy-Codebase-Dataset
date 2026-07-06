package rabinkarp;

import java.util.ArrayList;
import java.util.List;

public final class NaiveMatcher implements StringMatcher {
    @Override
    public List<Integer> search(CharSequence pattern, CharSequence text) {
        int m = pattern.length();
        int n = text.length();
        List<Integer> result = new ArrayList<>();
        TextWindow window = new TextWindow(text, 0, m);
        while (window.start() <= n - m) {
            if (window.startsWith(pattern)) result.add(window.start());
            window = window.slide();
        }
        return result;
    }
}
