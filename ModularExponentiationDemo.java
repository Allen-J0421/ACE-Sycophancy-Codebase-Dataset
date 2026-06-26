final class ModularExponentiationDemo {
    private ModularExponentiationDemo() {
    }

    public static void main(String[] args) {
        ModularExponentiationInput input = ModularExponentiationInput.fromArgs(args);
        System.out.println(ModularExponentiation.modPow(input));
    }
}
