import java.util.stream.Stream;

public interface PatternSearcher {
    SearchResult search(String text);

    default Stream<Integer> stream(String text) {
        return search(text).stream();
    }
}
