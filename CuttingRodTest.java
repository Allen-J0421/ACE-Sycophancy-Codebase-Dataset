class CuttingRodTest {
    public static void main(String[] args) {
        returnsBestRevenueForSamplePrices();
        returnsBestRevenueForExplicitRodLength();
        returnsZeroForOnlySentinelPrice();
        rejectsMissingPriceTable();
        rejectsEmptyPriceTable();
        rejectsNegativeRodLength();
        rejectsRodLengthOutsidePriceTable();
    }

    private static void returnsBestRevenueForSamplePrices() {
        int[] pricesByLength = {0, 1, 5, 8, 9, 10, 17, 17, 20};

        assertEquals(22, CuttingRod.cutRod(pricesByLength));
    }

    private static void returnsBestRevenueForExplicitRodLength() {
        int[] pricesByLength = {0, 1, 5, 8, 9, 10, 17, 17, 20};

        assertEquals(10, CuttingRod.cutRod(pricesByLength, 4));
    }

    private static void returnsZeroForOnlySentinelPrice() {
        int[] pricesByLength = {0};

        assertEquals(0, CuttingRod.cutRod(pricesByLength));
    }

    private static void rejectsMissingPriceTable() {
        assertThrowsIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CuttingRod.cutRod(null);
            }
        });
    }

    private static void rejectsEmptyPriceTable() {
        assertThrowsIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CuttingRod.cutRod(new int[0]);
            }
        });
    }

    private static void rejectsNegativeRodLength() {
        assertThrowsIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CuttingRod.cutRod(new int[] {0, 1}, -1);
            }
        });
    }

    private static void rejectsRodLengthOutsidePriceTable() {
        assertThrowsIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CuttingRod.cutRod(new int[] {0, 1}, 2);
            }
        });
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertThrowsIllegalArgument(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }

        throw new AssertionError("Expected IllegalArgumentException");
    }
}
