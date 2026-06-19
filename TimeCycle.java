
public enum TimeCycle {
	DAY,
	NIGHT;


	public TimeCycle toggle() {
		if (this == TimeCycle.DAY) {
			return TimeCycle.NIGHT;
		} else {
			return TimeCycle.DAY;
		}
	}
}
