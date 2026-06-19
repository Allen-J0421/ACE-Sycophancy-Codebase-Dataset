import java.util.Random;


@Deprecated
public class Randomizer {

	private Randomizer() {
	}


	public static Random getRandom() {
		return RandomService.shared().asJavaRandom();
	}


	public static void reset() {
		RandomService.shared().reset();
	}
}
