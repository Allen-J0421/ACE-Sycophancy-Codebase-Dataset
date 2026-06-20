import java.util.stream.Collectors;

public class StronglyConnectedComponentsFormatter {

    private StronglyConnectedComponentsFormatter() {}

    public static String format(StronglyConnectedComponentsResult result) {
        String sccLines = result.stream()
                .map(scc -> scc.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n"));
        return sccLines.isEmpty()
                ? "Strongly Connected Components:"
                : "Strongly Connected Components:\n" + sccLines;
    }
}
