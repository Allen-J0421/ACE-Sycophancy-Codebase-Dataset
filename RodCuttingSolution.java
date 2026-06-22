import java.util.List;

record RodCuttingSolution(int rodLength, int maxRevenue, List<Integer> cuts) {
    RodCuttingSolution {
        if (rodLength < 0) {
            throw new IllegalArgumentException("Rod length must not be negative.");
        }

        if (maxRevenue < 0) {
            throw new IllegalArgumentException("Maximum revenue must not be negative.");
        }

        cuts = List.copyOf(cuts);
        validateCuts(rodLength, cuts);
    }

    int pieceCount() {
        return cuts.size();
    }

    boolean isUncut() {
        return pieceCount() == 1 && cuts.get(0) == rodLength;
    }

    private static void validateCuts(int rodLength, List<Integer> cuts) {
        int totalLength = 0;

        for (int cutLength : cuts) {
            if (cutLength <= 0) {
                throw new IllegalArgumentException("Cut lengths must be positive.");
            }

            totalLength += cutLength;
        }

        if (totalLength != rodLength) {
            throw new IllegalArgumentException("Cut lengths must sum to rod length.");
        }
    }
}
