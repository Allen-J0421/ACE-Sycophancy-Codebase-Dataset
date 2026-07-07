import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PatternSearchDemo {

    public static void main(String[] args) throws Exception {
        String txt = "aabaacaadaabaaba";
        String pat = "aaba";

        PatternSearcher searcher = PatternSearchFactory.create(pat);

        // Eager: collect all results at once
        SearchResult result = searcher.search(txt);
        System.out.println("Eager  : " + result);

        // Lazy: stream positions, transform, collect only what's needed
        List<String> formatted = searcher.stream(txt)
                .map(pos -> "[" + pos + "]")
                .collect(Collectors.toList());
        System.out.println("Stream : " + formatted);

        // Lazy: find first match without scanning the rest
        searcher.stream(txt)
                .findFirst()
                .ifPresent(pos -> System.out.println("First  : " + pos));

        // Async: non-blocking search, chain post-processing on the result
        CompletableFuture<SearchResult> future = searcher.searchAsync(txt);
        future.thenAccept(r -> System.out.println("Async  : " + r))
              .get(); // block only here, at the demo boundary
    }
}
