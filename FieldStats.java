import java.util.LinkedHashMap;
import java.util.Map;


public class FieldStats {

	private final Map<Class<?>, Counter> counters;

	private boolean countsValid;


	public FieldStats() {
		counters = new LinkedHashMap<>();
		countsValid = true;
	}


	public String getPopulationDetails(Field field) {
		StringBuilder buffer = new StringBuilder();
		if (!countsValid) {
			generateCounts(field);
		}
		for (Counter info : counters.values()) {
			buffer.append(info.getName());
			buffer.append(": ");
			buffer.append(info.getCount());
			buffer.append(' ');
		}
		return buffer.toString();
	}


	public void reset() {
		countsValid = false;
		for (Counter count : counters.values()) {
			count.reset();
		}
	}


	public void incrementCount(Class<?> animalClass) {
		Counter count = counters.get(animalClass);
		if (count == null) {
			count = new Counter(animalClass.getName());
			counters.put(animalClass, count);
		}
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
		counters.clear();
		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				Object animal = field.getAnimalAt(row, col);
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
