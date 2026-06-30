final class RangeValidator {
    private RangeValidator() {
    }

    static void validate(int length, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex must be less than or equal to toIndex");
        }

        if (fromIndex < 0 || toIndex > length) {
            throw new IndexOutOfBoundsException("range must be within array bounds");
        }
    }
}
