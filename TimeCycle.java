
public enum TimeCycle {
	DAY,
	NIGHT;


	/** The cycle that follows this one. */
	public TimeCycle next() {
		return this == DAY ? NIGHT : DAY;
	}
}
