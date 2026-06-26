package euclidean;

final class ArgumentParser {

    private ArgumentParser() {
        // Utility class.
    }

    static Operands parseOperands(String first, String second) {
        return new Operands(
            parseIntArg(first, "first"),
            parseIntArg(second, "second")
        );
    }

    private static int parseIntArg(String value, String label) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid " + label + " integer: " + value, ex);
        }
    }
}
