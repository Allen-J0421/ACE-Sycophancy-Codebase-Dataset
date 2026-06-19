public enum Gender {
	FEMALE,
	MALE;


	public static Gender randomGender() {
		if (RandomService.shared().nextBoolean()) {
			return Gender.FEMALE;
		}
		return Gender.MALE;
	}
}
