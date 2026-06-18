import java.util.Random;

class RandomPivotSelector<T extends Comparable<T>> implements PivotSelector<T> {
    private final Random random;

    public RandomPivotSelector() {
        this(new Random());
    }

    public RandomPivotSelector(Random random) {
        this.random = random;
    }

    @Override
    public int selectPivot(T[] array, int low, int high) {
        int randomIndex = low + random.nextInt(high - low + 1);
        swap(array, randomIndex, high);
        return high;
    }

    private void swap(Object[] array, int i, int j) {
        Object temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
