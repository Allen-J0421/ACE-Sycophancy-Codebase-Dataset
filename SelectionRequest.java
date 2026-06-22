import java.util.Arrays;

final class SelectionRequest {

    private final int[] workingValues;
    private final int targetIndex;

    private SelectionRequest(int[] workingValues, int targetIndex) {
        this.workingValues = workingValues;
        this.targetIndex = targetIndex;
    }

    static SelectionRequest copying(int[] values, int rank) {
        validateRank(values, rank);
        return new SelectionRequest(Arrays.copyOf(values, values.length), rank - 1);
    }

    static SelectionRequest inPlace(int[] values, int rank) {
        validateRank(values, rank);
        return new SelectionRequest(values, rank - 1);
    }

    int selectWith(PivotSelector pivotSelector) {
        return QuickSelectEngine.select(this, pivotSelector);
    }

    int[] workingValues() {
        return workingValues;
    }

    int targetIndex() {
        return targetIndex;
    }

    private static void validateRank(int[] values, int rank) {
        if (values == null) {
            throw new IllegalArgumentException("Input array must not be null");
        }

        if (values.length == 0) {
            throw new IllegalArgumentException("Input array must not be empty");
        }

        if (rank < 1 || rank > values.length) {
            throw new IllegalArgumentException(
                "rank must be between 1 and " + values.length + " inclusive");
        }
    }
}
