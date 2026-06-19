public class SimulationSnapshot {

	private final int step;
	private final TimeCycle timeCycle;
	private final FieldEnvironment field;
	private final Climate climate;
	private final int sickPercentage;
	private final boolean viable;


	public SimulationSnapshot(int step, TimeCycle timeCycle, FieldEnvironment field, Climate climate,
			int sickPercentage, boolean viable) {
		this.step = step;
		this.timeCycle = timeCycle;
		this.field = field;
		this.climate = climate;
		this.sickPercentage = sickPercentage;
		this.viable = viable;
	}


	public int getStep() {
		return step;
	}


	public TimeCycle getTimeCycle() {
		return timeCycle;
	}


	public FieldEnvironment getField() {
		return field;
	}


	public Climate getClimate() {
		return climate;
	}


	public int getSickPercentage() {
		return sickPercentage;
	}


	public boolean isViable() {
		return viable;
	}
}
