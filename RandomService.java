import java.util.List;
import java.util.Random;


public final class RandomService {

	private static final int DEFAULT_SEED = 1111;

	private static final boolean USE_SHARED = true;

	private static final RandomService SHARED = new RandomService(new Random(DEFAULT_SEED));

	private final Random random;


	private RandomService(Random random) {
		this.random = random;
	}


	public static RandomService shared() {
		if (USE_SHARED) {
			return SHARED;
		}
		return new RandomService(new Random());
	}


	public boolean chance(double probability) {
		return random.nextDouble() <= probability;
	}


	public int nextInt(int exclusiveUpperBound) {
		return random.nextInt(exclusiveUpperBound);
	}


	public int nextIntInclusive(int minValue, int maxValue) {
		return random.nextInt((maxValue - minValue) + 1) + minValue;
	}


	public boolean oneIn(int bound) {
		return random.nextInt(bound) == 1;
	}


	public boolean nextBoolean() {
		return random.nextBoolean();
	}


	public <T> void shuffle(List<T> values) {
		for (int index = values.size() - 1; index > 0; index--) {
			int swapIndex = random.nextInt(index + 1);
			T currentValue = values.get(index);
			values.set(index, values.get(swapIndex));
			values.set(swapIndex, currentValue);
		}
	}


	public void reset() {
		if (USE_SHARED) {
			random.setSeed(DEFAULT_SEED);
		}
	}


	Random asJavaRandom() {
		return random;
	}
}
