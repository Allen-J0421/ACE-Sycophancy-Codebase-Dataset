record ModularExponentiationInput(int base, int exponent, int modulus) {
    ModularExponentiationInput {
        validateExponent(exponent);
        validateModulus(modulus);
    }

    private static void validateExponent(int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative.");
        }
    }

    private static void validateModulus(int modulus) {
        if (modulus <= 0) {
            throw new IllegalArgumentException("Modulus must be positive.");
        }
    }
}
