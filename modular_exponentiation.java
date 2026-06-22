class ModularExponentiation {
    public static int powMod(int base, int exponent, int modulus) {
        if (modulus <= 0) throw new IllegalArgumentException("modulus must be positive");
        if (exponent < 0) throw new IllegalArgumentException("exponent must be non-negative");
        if (modulus == 1) return 0;

        long result = 1;
        long b = base % modulus;

        while (exponent > 0) {
            if ((exponent & 1) == 1) {
                result = (result * b) % modulus;
            }
            b = (b * b) % modulus;
            exponent >>= 1;
        }

        return (int) result;
    }

    public static void main(String[] args) {
        System.out.println(powMod(3, 2, 4));   // 9 % 4 = 1
        System.out.println(powMod(2, 10, 1000)); // 1024 % 1000 = 24
    }
}
