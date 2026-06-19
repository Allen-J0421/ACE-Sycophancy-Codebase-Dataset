/**
 * Interface for formatting LCS computation results.
 * Enables different output formats (simple, detailed, JSON, etc.) without
 * modifying core computation logic.
 */
interface ResultFormatter {
    /**
     * Formats an LCS result for output.
     *
     * @param s1     first input string
     * @param s2     second input string
     * @param result LCS computation result
     * @return formatted result string ready for output
     */
    String format(String s1, String s2, LcsResult result);
}

/**
 * Simple text format: shows strings and LCS length.
 * Default format for CLI output.
 *
 * Example output:
 * String 1: "AGGTAB"
 * String 2: "GXTXAYB"
 * LCS Length: 4
 */
class SimpleResultFormatter implements ResultFormatter {
    @Override
    public String format(String s1, String s2, LcsResult result) {
        return "String 1: \"" + s1 + "\"\n" +
                "String 2: \"" + s2 + "\"\n" +
                "LCS Length: " + result.getLength();
    }
}

/**
 * Detailed format: includes additional information and analysis.
 * Shows strings, length, similarity ratio, and basic analysis.
 *
 * Example output:
 * ===== LCS Analysis =====
 * String 1: "AGGTAB" (length: 6)
 * String 2: "GXTXAYB" (length: 7)
 * LCS Length: 4
 * Similarity Ratio: 57.1%
 */
class DetailedResultFormatter implements ResultFormatter {
    @Override
    public String format(String s1, String s2, LcsResult result) {
        int minLen = Math.min(s1.length(), s2.length());
        double similarity = minLen > 0 ? (100.0 * result.getLength()) / minLen : 0;

        return "===== LCS Analysis =====\n" +
                "String 1: \"" + s1 + "\" (length: " + s1.length() + ")\n" +
                "String 2: \"" + s2 + "\" (length: " + s2.length() + ")\n" +
                "LCS Length: " + result.getLength() + "\n" +
                String.format("Similarity Ratio: %.1f%%\n", similarity) +
                "Min Length: " + minLen;
    }
}

/**
 * JSON format: outputs result as JSON for programmatic consumption.
 *
 * Example output:
 * {
 *   "string1": "AGGTAB",
 *   "string2": "GXTXAYB",
 *   "lcsLength": 4,
 *   "string1Length": 6,
 *   "string2Length": 7,
 *   "similarityRatio": 57.14
 * }
 */
class JsonResultFormatter implements ResultFormatter {
    @Override
    public String format(String s1, String s2, LcsResult result) {
        int minLen = Math.min(s1.length(), s2.length());
        double similarity = minLen > 0 ? (100.0 * result.getLength()) / minLen : 0;

        return "{\n" +
                "  \"string1\": \"" + escapeJson(s1) + "\",\n" +
                "  \"string2\": \"" + escapeJson(s2) + "\",\n" +
                "  \"lcsLength\": " + result.getLength() + ",\n" +
                "  \"string1Length\": " + s1.length() + ",\n" +
                "  \"string2Length\": " + s2.length() + ",\n" +
                String.format("  \"similarityRatio\": %.2f\n", similarity) +
                "}";
    }

    /**
     * Escapes special characters for JSON output.
     */
    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

/**
 * CSV format: outputs result as comma-separated values.
 * Useful for batch processing and data analysis.
 *
 * Example output:
 * "AGGTAB","GXTXAYB",4,6,7,57.14
 */
class CsvResultFormatter implements ResultFormatter {
    @Override
    public String format(String s1, String s2, LcsResult result) {
        int minLen = Math.min(s1.length(), s2.length());
        double similarity = minLen > 0 ? (100.0 * result.getLength()) / minLen : 0;

        return "\"" + escapeCsv(s1) + "\"," +
                "\"" + escapeCsv(s2) + "\"," +
                result.getLength() + "," +
                s1.length() + "," +
                s2.length() + "," +
                String.format("%.2f", similarity);
    }

    /**
     * Escapes special characters for CSV output.
     */
    private String escapeCsv(String s) {
        return s.replace("\"", "\"\"").replace("\n", " ");
    }
}

/**
 * Compact format: minimal output for scripting/automation.
 * Just the LCS length with no additional text.
 *
 * Example output:
 * 4
 */
class CompactResultFormatter implements ResultFormatter {
    @Override
    public String format(String s1, String s2, LcsResult result) {
        return String.valueOf(result.getLength());
    }
}
