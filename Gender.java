
/**
 * Enum describing the genders allowed for animals in this simulation
 * @version 2.2.3
 */
public enum Gender {
	MALE, 
	FEMALE;

	/**
	 * Method which inverts the gender. Useful to see which gender an animal should breed with 
	 * @return the opposite gender
	 */
	public Gender invert() {
		return this == Gender.MALE ? Gender.FEMALE : Gender.MALE;
	}
}