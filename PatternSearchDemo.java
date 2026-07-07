import java.util.List;
import java.util.stream.Collectors;

public class PatternSearchDemo {

    public static void main(String[] args) {
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
    }
}
