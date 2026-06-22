class EuclideanAlgorithms {

    private EuclideanAlgorithms() {}

    static class GcdResult {
        final int gcd, x, y;
        private final int a, b;

        GcdResult(int gcd, int x, int y, int a, int b) {
            this.gcd = gcd; this.x = x; this.y = y; this.a = a; this.b = b;
        }

        boolean verify() { return a * x + b * y == gcd; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof GcdResult)) return false;
            GcdResult other = (GcdResult) o;
            return gcd == other.gcd && x == other.x && y == other.y;
        }

        @Override
        public int hashCode() { return 31 * (31 * gcd + x) + y; }

        @Override
        public String toString() { return "gcd=" + gcd + ", x=" + x + ", y=" + y; }
    }

    // Finds gcd and Bézout coefficients: a*x + b*y = gcd
    static GcdResult extendedGcd(int a, int b) {
        validate(a, b);
        int prevR = a, currR = b;
        int prevS = 1, currS = 0;
        int prevT = 0, currT = 1;
        while (currR != 0) {
            int q = prevR / currR;
            int nextR = prevR - q * currR; prevR = currR; currR = nextR;
            int nextS = prevS - q * currS; prevS = currS; currS = nextS;
            int nextT = prevT - q * currT; prevT = currT; currT = nextT;
        }
        return new GcdResult(prevR, prevS, prevT, a, b);
    }

    static int gcd(int a, int b) {
        return extendedGcd(a, b).gcd;
    }

    static long lcm(int a, int b) {
        validate(a, b);
        if (a == 0 || b == 0) return 0;
        return (long) a / gcd(a, b) * b;
    }

    private static void validate(int a, int b) {
        if (a < 0 || b < 0)
            throw new IllegalArgumentException("Inputs must be non-negative");
    }

    public static void main(String[] args) {
        int a = 35, b = 15;
        GcdResult ext = extendedGcd(a, b);
        System.out.printf("GCD(%d, %d) = %d%n", a, b, gcd(a, b));
        System.out.printf("LCM(%d, %d) = %d%n", a, b, lcm(a, b));
        System.out.printf("Extended GCD: %s  (valid: %b)%n", ext, ext.verify());
    }
}
