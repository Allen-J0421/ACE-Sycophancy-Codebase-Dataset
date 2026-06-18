class EuclideanAlgorithm {

    static int findGCD(int a, int b) {
        if (a == 0)
            return b;
        return findGCD(b % a, a);
    }
    public static void main(String[] args) {
        int a = 35, b = 15;
        int g = findGCD(a, b);
        System.out.println(g);
    }
}
