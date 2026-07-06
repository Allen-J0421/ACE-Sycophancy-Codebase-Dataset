package euclidean;

final class ArgumentParser {

    private ArgumentParser() {}

    static Operands parseOperands(String a, String b) {
        return new Operands(parseIntArg(a, "first"), parseIntArg(b, "second"));
    }

    private static int parseIntArg(String value, String name) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " argument is not a valid integer: " + value, e);
        }
    }
}
