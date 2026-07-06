class ModularExponentiation {
    public int powMod(int base, int exp, int mod) {
        long result = 1;
        long b = base % mod;

        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = result * b % mod;
            }
            b = b * b % mod;
            exp >>= 1;
        }

        return (int) result;
    }

    public static void main(String[] args) {
        ModularExponentiation obj = new ModularExponentiation();
        System.out.println(obj.powMod(3, 2, 4));   // 9 % 4 = 1
        System.out.println(obj.powMod(2, 10, 1000)); // 1024 % 1000 = 24
        System.out.println(obj.powMod(5, 0, 7));    // 1
    }
}
