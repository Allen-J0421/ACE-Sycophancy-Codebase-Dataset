record ModularExponentiationInput(int base, int exponent, int modulus) {
    ModularExponentiationInput {
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative.");
        }

        if (modulus <= 0) {
            throw new IllegalArgumentException("Modulus must be positive.");
        }
    }
}
