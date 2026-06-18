public final class CacheCapacity {
    private final int value;

    private CacheCapacity(int value) {
        this.value = value;
    }

    public static CacheCapacity of(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("capacity must be greater than 0");
        }

        return new CacheCapacity(value);
    }

    public int value() {
        return value;
    }

    public int mapCapacityFor(float loadFactor) {
        return Math.max(1, (int) Math.ceil(value / loadFactor));
    }
}
