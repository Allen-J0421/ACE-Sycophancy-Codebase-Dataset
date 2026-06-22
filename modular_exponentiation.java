import java.math.BigInteger;
import java.util.Objects;

class ModularExponentiation {
    private ModularExponentiation() {}

    public static int powMod(int base, int exponent, int modulus) {
        return (int) powMod((long) base, (long) exponent, (long) modulus);
    }

    public static long powMod(long base, long exponent, long modulus) {
        return powMod(BigInteger.valueOf(base), BigInteger.valueOf(exponent), BigInteger.valueOf(modulus))
                .longValue();
    }

    public static BigInteger powMod(BigInteger base, BigInteger exponent, BigInteger modulus) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(exponent, "exponent");
        Objects.requireNonNull(modulus, "modulus");
        if (modulus.signum() <= 0)
            throw new IllegalArgumentException("modulus must be positive, got: " + modulus);
        if (exponent.signum() < 0)
            throw new IllegalArgumentException("exponent must be non-negative, got: " + exponent);
        return base.modPow(exponent, modulus);
    }

    public static void main(String[] args) {
        System.out.println(powMod(3, 2, 4));                         // 9 % 4 = 1
        System.out.println(powMod(2, 10, 1000));                     // 1024 % 1000 = 24
        System.out.println(powMod(2L, 62L, 1_000_000_007L));        // large modulus
        System.out.println(powMod(                                   // arbitrary precision
                BigInteger.valueOf(2),
                BigInteger.valueOf(1000),
                BigInteger.valueOf(1_000_000_007L)));
    }
}
