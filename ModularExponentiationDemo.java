final class ModularExponentiationDemo {
    private ModularExponentiationDemo() {
    }

    public static void main(String[] args) {
        ModularExponentiationInput input = ModularExponentiationInputParser.parse(args);
        System.out.println(ModularExponentiation.modPow(input));
    }
}
