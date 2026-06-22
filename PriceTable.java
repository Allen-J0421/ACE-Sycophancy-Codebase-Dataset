final class PriceTable {
    private final LengthIndexedValues pricesByLength;

    private PriceTable(LengthIndexedValues pricesByLength) {
        this.pricesByLength = pricesByLength;
    }

    static PriceTable fromSentinelArray(int[] priceTableWithSentinel) {
        if (priceTableWithSentinel == null || priceTableWithSentinel.length == 0) {
            throw new IllegalArgumentException("Price table must contain a sentinel at index 0.");
        }

        return new PriceTable(
                LengthIndexedValues.copying(1, sliceSentinelValue(priceTableWithSentinel))
        );
    }

    static PriceTable fromPricesByLength(int[] pricesByLength) {
        return new PriceTable(LengthIndexedValues.copying(1, pricesByLength));
    }

    int maxLength() {
        return pricesByLength.maximumLength();
    }

    int priceFor(int rodLength) {
        return pricesByLength.valueAt(rodLength);
    }

    private static int[] sliceSentinelValue(int[] priceTableWithSentinel) {
        int[] pricesByLength = new int[priceTableWithSentinel.length - 1];

        for (int index = 1; index < priceTableWithSentinel.length; index++) {
            pricesByLength[index - 1] = priceTableWithSentinel[index];
        }

        return pricesByLength;
    }
}
