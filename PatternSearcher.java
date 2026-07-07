import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface PatternSearcher {
    SearchResult search(String text);

    default Stream<Integer> stream(String text) {
        return search(text).stream();
    }

    default CompletableFuture<SearchResult> searchAsync(String text) {
        return CompletableFuture.supplyAsync(() -> search(text));
    }
}
