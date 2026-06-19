import java.util.Random;


public enum Gender {
	FEMALE,
	MALE;


	public static Gender randomGender() {
		Random rand = Randomizer.getRandom();
		return rand.nextInt(2) == 0 ? FEMALE : MALE;
	}
}
