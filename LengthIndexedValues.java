class LengthIndexedValues {
    private final int[] values;

    LengthIndexedValues(int size) {
        values = new int[size + 1];
    }

    int get(int length) {
        return values[length];
    }

    void set(int length, int value) {
        values[length] = value;
    }
}
