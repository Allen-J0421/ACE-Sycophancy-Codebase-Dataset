import java.util.Random;


public enum Gender {
	FEMALE,
	MALE;

	private static final Random rand = Randomizer.getRandom();

	public static Gender randomGender() {
		return rand.nextInt(2) == 0 ? FEMALE : MALE;
	}
}
