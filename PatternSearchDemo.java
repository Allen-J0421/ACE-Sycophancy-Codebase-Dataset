import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PatternSearchDemo {

    public static void main(String[] args) throws Exception {
        String txt = "aabaacaadaabaaba";
        String pat = "aaba";

        PatternSearcher naive = PatternSearchFactory.create(pat, PatternSearchFactory.Algorithm.NAIVE);
        PatternSearcher kmp   = PatternSearchFactory.create(pat, PatternSearchFactory.Algorithm.KMP);

        // Eager
        System.out.println("Naive  : " + naive.search(txt));
        System.out.println("KMP    : " + kmp.search(txt));

        // Lazy stream
        List<String> formatted = kmp.stream(txt)
                .map(pos -> "[" + pos + "]")
                .collect(Collectors.toList());
        System.out.println("Stream : " + formatted);

        // Async
        CompletableFuture<SearchResult> future = kmp.searchAsync(txt);
        future.thenAccept(r -> System.out.println("Async  : " + r)).get();
    }
}
