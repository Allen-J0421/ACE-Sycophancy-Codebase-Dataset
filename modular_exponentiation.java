class ModularExponentiation {
    public int powMod(int x, int n, int M) {
        int res = 1;

        while (n >= 1) {

            if ((n & 1) == 1) {
                res = (int)((1L * res * x) % M);

                n--;
             } else {

                x = (int)((1L * x * x) % M);
                n /= 2;
            }
        }

        return res;
    }

    public static void main(String[] args) {
        int x = 3, n = 2, M = 4;
        ModularExponentiation obj = new ModularExponentiation();
        System.out.println(obj.powMod(x, n, M));
    }
}
