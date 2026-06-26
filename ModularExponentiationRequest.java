public record ModularExponentiationRequest(int base, int exponent, int modulus) {
    public ModularExponentiationRequest {
        if (modulus <= 0) {
            throw new IllegalArgumentException("Modulus must be positive.");
        }
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative.");
        }
    }
}
