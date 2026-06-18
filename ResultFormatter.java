import java.util.Objects;
import java.util.stream.IntStream;

class ResultFormatter {
    private final AlgorithmConfig config;

    ResultFormatter(AlgorithmConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    void printDistances(ShortestPathResult result) {
        result.getDistances().stream()
            .forEach(distance -> System.out.print(distance + " "));
        System.out.println();
    }

    String formatResult(ShortestPathResult result) {
        return result.getDistances().toString();
    }

    void printPaths(ShortestPathResult result) {
        if (!config.shouldTrackPaths()) {
            return;
        }

        result.getAllPaths().forEach((node, pathOpt) ->
            pathOpt.ifPresent(path -> System.out.println(
                String.format("To node %d: %s (distance=%d, hops=%d)",
                            node, path.getNodes(), path.getTotalDistance(), path.getLength() - 1)
            ))
        );
    }

    void printSummary(ShortestPathResult result) {
        if (!config.isVerbose()) {
            return;
        }

        int reachableCount = (int) IntStream.range(0, result.getDistances().size())
            .filter(result::isReachable)
            .count();

        System.out.println(String.format(
            "Summary: From node %d, %d/%d nodes are reachable",
            result.getSourceNode(), reachableCount, result.getDistances().size()
        ));
    }
}
