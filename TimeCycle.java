
public enum TimeCycle {
	DAY,
	NIGHT;


	public TimeCycle toggleTimeCycle(TimeCycle currentTimeCycle) {
		if (currentTimeCycle == TimeCycle.DAY) {
			return TimeCycle.NIGHT;
		} else {
			return TimeCycle.DAY;
		}
	}
}
