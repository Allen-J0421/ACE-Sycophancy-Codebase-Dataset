import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class FieldStats {

	private final Map<Class<?>, Long> counters;

	private boolean countsValid;


	public FieldStats() {
		counters = new LinkedHashMap<>();
		countsValid = true;
	}


	public String getPopulationDetails(FieldEnvironment field) {
		if (!countsValid) {
			generateCounts(field);
		}
		return counters.entrySet().stream()
				.map(entry -> entry.getKey().getName() + ": " + entry.getValue())
				.collect(Collectors.joining(" "));
	}


	public void reset() {
		countsValid = false;
		counters.clear();
	}


	public void incrementCount(Class<?> animalClass) {
		counters.merge(animalClass, 1L, Long::sum);
	}


	public void countFinished() {
		countsValid = true;
	}


	public boolean isViable(FieldEnvironment field) {
		if (!countsValid) {
			generateCounts(field);
		}
		return counters.values().stream()
				.filter(count -> count > 0)
				.count() > 1;
	}


	private void generateCounts(FieldEnvironment field) {
		reset();
		counters.putAll(field.streamAnimals()
				.collect(Collectors.groupingBy(Animal::getClass, LinkedHashMap::new, Collectors.counting())));
		countsValid = true;
	}


	public int getPopulationCount(FieldEnvironment field, Class<?> key) {
		if (!countsValid) {
			generateCounts(field);
		}
		return counters.getOrDefault(key, 0L).intValue();
	}
}
