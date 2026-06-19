import java.util.HashMap;
import java.util.Map;


/**
 * Tallies the population of each species on the field and answers questions
 * the views need: per-species counts, a human-readable summary, and whether
 * the simulation is still viable (more than one species alive).
 */
public class FieldStats {

	private final Map<Class<?>, Counter> counters;

	private boolean countsValid;


	public FieldStats() {
		counters = new HashMap<>();
		countsValid = true;
	}


	public String getPopulationDetails(Field field) {
		if (!countsValid) {
			generateCounts(field);
		}
		StringBuilder buffer = new StringBuilder();
		for (Counter info : counters.values()) {
			buffer.append(info.getName())
					.append(": ")
					.append(info.getCount())
					.append(' ');
		}
		return buffer.toString();
	}


	public void reset() {
		countsValid = false;
		for (Counter counter : counters.values()) {
			counter.reset();
		}
	}


	public void incrementCount(Class<?> animalClass) {
		Counter count = counters.computeIfAbsent(animalClass, c -> new Counter(c.getName()));
		count.increment();
	}


	public void countFinished() {
		countsValid = true;
	}


	public boolean isViable(Field field) {
		if (!countsValid) {
			generateCounts(field);
		}
		int nonZero = 0;
		for (Counter info : counters.values()) {
			if (info.getCount() > 0) {
				nonZero++;
			}
		}
		return nonZero > 1;
	}


	private void generateCounts(Field field) {
		reset();
		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				Animal animal = field.getAnimalAt(row, col);
				if (animal != null) {
					incrementCount(animal.getClass());
				}
			}
		}
		countsValid = true;
	}


	public int getPopulationCount(Field field, Class<?> key) {
		if (!countsValid) {
			generateCounts(field);
		}
		Counter counter = counters.get(key);
		return counter == null ? 0 : counter.getCount();
	}
}
