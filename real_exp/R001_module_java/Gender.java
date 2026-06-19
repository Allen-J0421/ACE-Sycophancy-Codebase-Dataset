import java.util.Random;


public enum Gender {
	FEMALE,
	MALE;


	public Gender randomGender() {
		Random rand = Randomizer.getRandom();
		if (rand.nextInt(2) == 0) {
			return Gender.FEMALE;
		}
		return Gender.MALE;
	}
}
