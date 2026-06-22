import java.util.Arrays;

final class PriceTable {
    private final int[] pricesByLength;

    private PriceTable(int[] pricesByLength) {
        this.pricesByLength = pricesByLength;
    }

    static PriceTable fromSentinelArray(int[] priceTableWithSentinel) {
        if (priceTableWithSentinel == null || priceTableWithSentinel.length == 0) {
            throw new IllegalArgumentException("Price table must contain a sentinel at index 0.");
        }

        return new PriceTable(
                Arrays.copyOfRange(priceTableWithSentinel, 1, priceTableWithSentinel.length)
        );
    }

    static PriceTable fromPricesByLength(int[] pricesByLength) {
        if (pricesByLength == null) {
            throw new IllegalArgumentException("Prices by length must not be null.");
        }

        return new PriceTable(Arrays.copyOf(pricesByLength, pricesByLength.length));
    }

    int maxLength() {
        return pricesByLength.length;
    }

    int priceFor(int rodLength) {
        validateLength(rodLength);
        return pricesByLength[rodLength - 1];
    }

    private void validateLength(int rodLength) {
        if (rodLength <= 0 || rodLength > maxLength()) {
            throw new IllegalArgumentException("Rod length is out of bounds: " + rodLength);
        }
    }
}
