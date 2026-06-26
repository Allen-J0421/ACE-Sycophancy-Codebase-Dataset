public final class ModularExponentiationApp {
    private ModularExponentiationApp() {
        // Application entrypoint only.
    }

    public static void main(String[] args) {
        ModularExponentiationRequest request = ModularExponentiationRequestParser.parse(args);

        System.out.println(ModularExponentiation.powMod(
            request.base(),
            request.exponent(),
            request.modulus()
        ));
    }
}
