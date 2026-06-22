import java.math.BigInteger;

class ModularExponentiation {
    private ModularExponentiation() {}

    public static int powMod(int base, int exponent, int modulus) {
        return (int) powMod((long) base, (long) exponent, (long) modulus);
    }

    public static long powMod(long base, long exponent, long modulus) {
        if (modulus <= 0) throw new IllegalArgumentException("modulus must be positive");
        if (exponent < 0) throw new IllegalArgumentException("exponent must be non-negative");
        return BigInteger.valueOf(base)
                         .modPow(BigInteger.valueOf(exponent), BigInteger.valueOf(modulus))
                         .longValue();
    }

    public static void main(String[] args) {
        System.out.println(powMod(3, 2, 4));                    // 9 % 4 = 1
        System.out.println(powMod(2, 10, 1000));                // 1024 % 1000 = 24
        System.out.println(powMod(2L, 62L, 1_000_000_007L));   // large modulus
    }
}
