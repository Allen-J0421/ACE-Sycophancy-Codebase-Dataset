enum MedianOfThreePivotSelector implements PivotSelector {
    INSTANCE;

    @Override
    public int selectPivotIndex(int[] values, int low, int high) {
        int mid = low + (high - low) / 2;

        order(values, low, mid);
        order(values, low, high);
        order(values, mid, high);

        return mid;
    }

    private void order(int[] values, int left, int right) {
        if (values[left] > values[right]) {
            int temp = values[left];
            values[left] = values[right];
            values[right] = temp;
        }
    }
}
