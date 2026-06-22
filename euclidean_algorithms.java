class EuclideanAlgorithms {

    static class GcdResult {
        final int gcd, x, y;
        GcdResult(int gcd, int x, int y) { this.gcd = gcd; this.x = x; this.y = y; }

        boolean verify(int a, int b) { return a * x + b * y == gcd; }

        @Override
        public String toString() { return "gcd=" + gcd + ", x=" + x + ", y=" + y; }
    }

    // Finds gcd and Bézout coefficients: a*x + b*y = gcd
    static GcdResult extendedGcd(int a, int b) {
        validate(a, b);
        int oldR = a, r = b;
        int oldS = 1, s = 0;
        int oldT = 0, t = 1;
        while (r != 0) {
            int q = oldR / r;
            int tmp = r; r = oldR - q * r; oldR = tmp;
            tmp = s; s = oldS - q * s; oldS = tmp;
            tmp = t; t = oldT - q * t; oldT = tmp;
        }
        return new GcdResult(oldR, oldS, oldT);
    }

    static int gcd(int a, int b) {
        validate(a, b);
        return gcdHelper(a, b);
    }

    static long lcm(int a, int b) {
        validate(a, b);
        if (a == 0 || b == 0) return 0;
        return (long) a / gcdHelper(a, b) * b;
    }

    private static int gcdHelper(int a, int b) {
        while (a != 0) { int t = a; a = b % a; b = t; }
        return b;
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
        System.out.printf("Extended GCD: %s  (valid: %b)%n", ext, ext.verify(a, b));
    }
}
