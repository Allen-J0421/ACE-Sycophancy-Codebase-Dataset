
public enum Season {
	SPRING,
	SUMMER,
	AUTUMN,
	WINTER;


	public static Season forStep(int step, int seasonLength) {
		Season[] seasons = values();
		return seasons[(step / seasonLength) % seasons.length];
	}
}
