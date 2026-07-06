package rabinkarp;

import java.util.ArrayList;
import java.util.List;

public final class NaiveMatcher implements StringMatcher {
    @Override
    public List<Integer> search(CharSequence pattern, CharSequence text) {
        int m = pattern.length();
        int n = text.length();
        List<Integer> result = new ArrayList<>();
        outer:
        for (int i = 0; i <= n - m; i++) {
            for (int j = 0; j < m; j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) continue outer;
            }
            result.add(i);
        }
        return result;
    }
}
