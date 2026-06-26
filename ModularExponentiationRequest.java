public final class ModularExponentiationRequest {
    private final int base;
    private final int exponent;
    private final int modulus;

    private ModularExponentiationRequest(int base, int exponent, int modulus) {
        this.base = base;
        this.exponent = exponent;
        this.modulus = modulus;
    }

    public static ModularExponentiationRequest of(int base, int exponent, int modulus) {
        return new ModularExponentiationRequest(base, exponent, modulus);
    }

    public int base() {
        return base;
    }

    public int exponent() {
        return exponent;
    }

    public int modulus() {
        return modulus;
    }
}
