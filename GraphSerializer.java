import java.io.*;

public class GraphSerializer {

    public static String toJSON(Graph graph) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"vertices\": ").append(graph.getVertexCount()).append(",\n");
        json.append("  \"edges\": [\n");

        boolean first = true;
        for (int i = 0; i < graph.getVertexCount(); i++) {
            for (int neighbor : graph.getAdjacent(i)) {
                if (!first) json.append(",\n");
                json.append(String.format("    {\"from\": %d, \"to\": %d}", i, neighbor));
                first = false;
            }
        }

        json.append("\n  ]\n");
        json.append("}");
        return json.toString();
    }

    public static String toDOT(Graph graph) {
        StringBuilder dot = new StringBuilder();
        dot.append("graph {\n");

        for (int i = 0; i < graph.getVertexCount(); i++) {
            for (int neighbor : graph.getAdjacent(i)) {
                if (i < neighbor) {
                    dot.append(String.format("  %d -- %d;\n", i, neighbor));
                }
            }
        }

        dot.append("}");
        return dot.toString();
    }

    public static void writeToFile(Graph graph, String filename, Format format) throws IOException {
        String content = format == Format.JSON ? toJSON(graph) : toDOT(graph);

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
            Logger.info("Graph serialized to " + filename);
        }
    }

    public static String toString(Graph graph) {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph{vertices=").append(graph.getVertexCount()).append(", edges=[");

        boolean first = true;
        for (int i = 0; i < graph.getVertexCount(); i++) {
            for (int neighbor : graph.getAdjacent(i)) {
                if (!first) sb.append(", ");
                sb.append(i).append("->").append(neighbor);
                first = false;
            }
        }

        sb.append("]}");
        return sb.toString();
    }

    public enum Format {
        JSON, DOT
    }
}
