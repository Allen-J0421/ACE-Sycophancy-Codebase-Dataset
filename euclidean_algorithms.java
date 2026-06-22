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
        return extendedGcdHelper(a, b);
    }

    static int gcdRecursive(int a, int b) {
        validate(a, b);
        return gcdHelper(a, b);
    }

    static int gcdIterative(int a, int b) {
        validate(a, b);
        while (a != 0) {
            int temp = a;
            a = b % a;
            b = temp;
        }
        return b;
    }

    static long lcm(int a, int b) {
        validate(a, b);
        if (a == 0 || b == 0) return 0;
        return (long) a / gcdIterative(a, b) * b;
    }

    private static GcdResult extendedGcdHelper(int a, int b) {
        if (a == 0) return new GcdResult(b, 0, 1);
        GcdResult r = extendedGcdHelper(b % a, a);
        return new GcdResult(r.gcd, r.y - (b / a) * r.x, r.x);
    }

    private static int gcdHelper(int a, int b) {
        if (a == 0) return b;
        return gcdHelper(b % a, a);
    }

    private static void validate(int a, int b) {
        if (a < 0 || b < 0)
            throw new IllegalArgumentException("Inputs must be non-negative");
    }

    public static void main(String[] args) {
        int a = 35, b = 15;
        GcdResult ext = extendedGcd(a, b);
        System.out.printf("GCD(%d, %d) = %d%n", a, b, gcdRecursive(a, b));
        System.out.printf("GCD(%d, %d) = %d (iterative)%n", a, b, gcdIterative(a, b));
        System.out.printf("LCM(%d, %d) = %d%n", a, b, lcm(a, b));
        System.out.printf("Extended GCD: %s  (valid: %b)%n", ext, ext.verify(a, b));
    }
}
