class ModularExponentiation {
    public int powMod(int base, int exponent, int modulus) {
        int result = 1;
        int currentBase = base;

        while (exponent >= 1) {
            if ((exponent & 1) == 1) {
                result = multiplyMod(result, currentBase, modulus);
            }

            currentBase = multiplyMod(currentBase, currentBase, modulus);
            exponent /= 2;
        }

        return result;
    }

    private int multiplyMod(int left, int right, int modulus) {
        return (int) ((1L * left * right) % modulus);
    }

    public static void main(String[] args) {
        int base = 3;
        int exponent = 2;
        int modulus = 4;

        ModularExponentiation calculator = new ModularExponentiation();
        System.out.println(calculator.powMod(base, exponent, modulus));
    }
}
