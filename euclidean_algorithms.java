class EuclideanAlgorithms {

    static int gcdRecursive(int a, int b) {
        if (a < 0 || b < 0)
            throw new IllegalArgumentException("Inputs must be non-negative");
        if (a == 0) return b;
        return gcdRecursive(b % a, a);
    }

    static int gcdIterative(int a, int b) {
        if (a < 0 || b < 0)
            throw new IllegalArgumentException("Inputs must be non-negative");
        while (a != 0) {
            int temp = a;
            a = b % a;
            b = temp;
        }
        return b;
    }

    static long lcm(int a, int b) {
        if (a == 0 || b == 0) return 0;
        return (long) a / gcdIterative(a, b) * b;
    }

    public static void main(String[] args) {
        int a = 35, b = 15;
        System.out.println("GCD(" + a + ", " + b + ") = " + gcdRecursive(a, b));
        System.out.println("GCD(" + a + ", " + b + ") = " + gcdIterative(a, b) + " (iterative)");
        System.out.println("LCM(" + a + ", " + b + ") = " + lcm(a, b));
    }
}
