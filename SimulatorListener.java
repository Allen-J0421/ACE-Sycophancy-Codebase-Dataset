
public interface SimulatorListener {

	void onStep(int step, TimeCycle timeCycle, Field field, Climate climate, int sickPercentage);

	void onReset(int step, TimeCycle timeCycle, Field field, Climate climate, int sickPercentage);
}
