import java.util.Random;


public class Randomizer {

	private static final int SEED = 1111;

	private static final Random rand = new Random(SEED);


	private Randomizer() {
	}


	public static Random getRandom() {
		return rand;
	}


	public static void reset() {
		rand.setSeed(SEED);
	}
}
