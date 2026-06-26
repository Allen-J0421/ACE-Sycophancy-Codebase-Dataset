class EuclideanAlgorithm {

    private static final int DEFAULT_A = 35;
    private static final int DEFAULT_B = 15;

    private EuclideanAlgorithm() {
        // Utility class.
    }

    static int findGCD(int a, int b) {
        long x = Math.abs((long) a);
        long y = Math.abs((long) b);

        if (x == 0) {
            return toIntExact(y);
        }
        if (y == 0) {
            return toIntExact(x);
        }

        while (y != 0) {
            long remainder = x % y;
            x = y;
            y = remainder;
        }

        return toIntExact(x);
    }

    public static void main(String[] args) {
        int a = DEFAULT_A;
        int b = DEFAULT_B;

        if (args.length == 2) {
            a = parseIntArg(args[0], "first");
            b = parseIntArg(args[1], "second");
        } else if (args.length != 0) {
            throw new IllegalArgumentException(
                "Expected either zero arguments or two integers: <a> <b>"
            );
        }

        System.out.println(findGCD(a, b));
    }

    private static int parseIntArg(String value, String label) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid " + label + " integer: " + value, ex);
        }
    }

    private static int toIntExact(long value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new ArithmeticException("GCD does not fit in an int: " + value);
        }
        return (int) value;
    }
}
