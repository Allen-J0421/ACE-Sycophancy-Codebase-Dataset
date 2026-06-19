
public enum Gender {
	FEMALE,
	MALE;


	/** A uniformly random gender. */
	public static Gender random() {
		return Randomizer.getRandom().nextInt(2) == 0 ? FEMALE : MALE;
	}
}
