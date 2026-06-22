public final class CuttingRodDemo {

    private CuttingRodDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        int[] samplePrices = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        System.out.println(CuttingRod.cutRod(samplePrices));
    }
}
