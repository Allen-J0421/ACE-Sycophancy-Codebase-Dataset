/**
 * Utility class for validating sorting inputs.
 * Provides comprehensive validation to prevent errors and improve debugging.
 */
class InputValidator {

    /**
     * Validates that an array is not null and has at least one element.
     */
    public static <T> void validateArray(T[] array, String paramName) {
        if (array == null) {
            throw new SortingException(
                SortingException.ErrorType.INVALID_ARRAY,
                paramName + " cannot be null"
            );
        }
    }

    /**
     * Validates array bounds (low and high indices).
     */
    public static <T> void validateBounds(T[] array, int low, int high, String paramName) {
        validateArray(array, paramName);

        if (low < 0) {
            throw new SortingException(
                SortingException.ErrorType.INVALID_BOUNDS,
                "low index cannot be negative: " + low
            );
        }

        if (high >= array.length) {
            throw new SortingException(
                SortingException.ErrorType.INVALID_BOUNDS,
                "high index out of bounds: " + high + " >= " + array.length
            );
        }

        if (low > high) {
            throw new SortingException(
                SortingException.ErrorType.INVALID_BOUNDS,
                "low (" + low + ") cannot be greater than high (" + high + ")"
            );
        }
    }

    /**
     * Validates configuration settings.
     */
    public static void validateConfiguration(SortingConfiguration config) {
        if (config == null) {
            throw new SortingException(
                SortingException.ErrorType.INVALID_CONFIGURATION,
                "Configuration cannot be null"
            );
        }

        if (config.getInsertionSortThreshold() < 0) {
            throw new SortingException(
                SortingException.ErrorType.INVALID_CONFIGURATION,
                "Insertion sort threshold cannot be negative: " +
                config.getInsertionSortThreshold()
            );
        }

        if (config.getIntroSortMaxDepth() < 1) {
            throw new SortingException(
                SortingException.ErrorType.INVALID_CONFIGURATION,
                "IntroSort max depth must be at least 1: " +
                config.getIntroSortMaxDepth()
            );
        }
    }

    /**
     * Validates pivot index.
     */
    public static void validatePivotIndex(int pivotIndex, int low, int high) {
        if (pivotIndex < low || pivotIndex > high) {
            throw new SortingException(
                SortingException.ErrorType.INVALID_BOUNDS,
                "Pivot index " + pivotIndex + " outside range [" + low + ", " + high + "]"
            );
        }
    }
}
