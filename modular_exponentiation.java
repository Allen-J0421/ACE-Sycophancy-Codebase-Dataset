class ModularExponentiation {
    public int powMod(int base, int exponent, int modulus) {
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
        int base = 3, exponent = 2, modulus = 4;
        ModularExponentiation obj = new ModularExponentiation();
        System.out.println(obj.powMod(base, exponent, modulus));
    }
}
