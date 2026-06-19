import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


public class PopulationChangedEvent extends AbstractSimulationEvent {

	private final Map<Class<?>, Integer> previousPopulation;

	private final Map<Class<?>, Integer> currentPopulation;


	public PopulationChangedEvent(SimulationSnapshot snapshot, Map<Class<?>, Integer> previousPopulation,
			Map<Class<?>, Integer> currentPopulation) {
		super(snapshot);
		this.previousPopulation = Collections.unmodifiableMap(new LinkedHashMap<>(previousPopulation));
		this.currentPopulation = Collections.unmodifiableMap(new LinkedHashMap<>(currentPopulation));
	}


	public Map<Class<?>, Integer> getPreviousPopulation() {
		return previousPopulation;
	}


	public Map<Class<?>, Integer> getCurrentPopulation() {
		return currentPopulation;
	}
}
