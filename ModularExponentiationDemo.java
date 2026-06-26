final class ModularExponentiationDemo {
    private static final String USAGE = "Expected either no arguments or: <base> <exponent> <modulus>.";
    private static final DemoInput SAMPLE_INPUT = new DemoInput(3, 2, 4);

    private ModularExponentiationDemo() {
    }

    public static void main(String[] args) {
        DemoInput input = parseInput(args);
        System.out.println(ModularExponentiation.modPow(input.base(), input.exponent(), input.modulus()));
    }

    private static DemoInput parseInput(String[] args) {
        return switch (args.length) {
            case 0 -> SAMPLE_INPUT;
            case 3 -> new DemoInput(
                    parseInt(args[0], "base"),
                    parseInt(args[1], "exponent"),
                    parseInt(args[2], "modulus"));
            default -> throw new IllegalArgumentException(USAGE);
        };
    }

    private static int parseInt(String rawValue, String name) {
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer for " + name + ": " + rawValue, exception);
        }
    }

    private record DemoInput(int base, int exponent, int modulus) {
    }
}
