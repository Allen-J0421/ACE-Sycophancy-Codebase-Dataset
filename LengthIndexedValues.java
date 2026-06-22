import java.util.Arrays;

final class LengthIndexedValues {
    private final int minimumLength;
    private final int[] valuesByLength;

    private LengthIndexedValues(int minimumLength, int[] valuesByLength) {
        this.minimumLength = minimumLength;
        this.valuesByLength = valuesByLength;
    }

    static LengthIndexedValues copying(int minimumLength, int[] valuesByLength) {
        if (valuesByLength == null) {
            throw new IllegalArgumentException("Values by length must not be null.");
        }

        return new LengthIndexedValues(
                minimumLength,
                Arrays.copyOf(valuesByLength, valuesByLength.length)
        );
    }

    static LengthIndexedValues initializedToZero(int minimumLength, int maximumLength) {
        if (maximumLength < minimumLength - 1) {
            throw new IllegalArgumentException("Maximum length must not be below the minimum.");
        }

        return new LengthIndexedValues(
                minimumLength,
                new int[maximumLength - minimumLength + 1]
        );
    }

    int maximumLength() {
        return minimumLength + valuesByLength.length - 1;
    }

    int valueAt(int rodLength) {
        validateLength(rodLength);
        return valuesByLength[toArrayIndex(rodLength)];
    }

    void setValueAt(int rodLength, int value) {
        validateLength(rodLength);
        valuesByLength[toArrayIndex(rodLength)] = value;
    }

    private int toArrayIndex(int rodLength) {
        return rodLength - minimumLength;
    }

    private void validateLength(int rodLength) {
        if (rodLength < minimumLength || rodLength > maximumLength()) {
            throw new IllegalArgumentException("Rod length is out of bounds: " + rodLength);
        }
    }
}
