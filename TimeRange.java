public record TimeRange(int start, int finish) {
    public TimeRange {
        validate(start, finish);
    }

    private static void validate(int start, int finish) {
        if (start > finish) {
            throw new IllegalArgumentException(
                "start time " + start + " cannot be greater than finish time " + finish);
        }
    }
}
