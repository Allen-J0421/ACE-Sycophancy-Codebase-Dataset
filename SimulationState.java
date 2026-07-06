
public class SimulationState {

	private final int step;

	private final TimeCycle timeCycle;

	private final Field field;

	private final Climate climate;

	private final int sickPercentage;


	public SimulationState(int step, TimeCycle timeCycle, Field field, Climate climate, int sickPercentage) {
		this.step = step;
		this.timeCycle = timeCycle;
		this.field = field;
		this.climate = climate;
		this.sickPercentage = sickPercentage;
	}


	public int getStep() {
		return step;
	}


	public TimeCycle getTimeCycle() {
		return timeCycle;
	}


	public Field getField() {
		return field;
	}


	public Climate getClimate() {
		return climate;
	}


	public int getSickPercentage() {
		return sickPercentage;
	}
}
