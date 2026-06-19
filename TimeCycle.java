
public enum TimeCycle {
	DAY,
	NIGHT;


	public TimeCycle toggle() {
		return this == DAY ? NIGHT : DAY;
	}
}
