package naivepatternsearch;

import java.util.List;

@FunctionalInterface
public interface PatternSearcher {
    List<Integer> search(CharSequence pattern, CharSequence text);
}
