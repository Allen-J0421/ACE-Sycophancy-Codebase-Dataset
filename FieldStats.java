import java.util.LinkedHashMap;
import java.util.Map;


public final class FieldStats {

	private final Map<Class<?>, Counter> counters;


	public FieldStats() {
		counters = new LinkedHashMap<>();
	}


	public String getPopulationDetails() {
		StringBuilder buffer = new StringBuilder();
		for (Counter info : counters.values()) {
			buffer.append(info.getName());
			buffer.append(": ");
			buffer.append(info.getCount());
			buffer.append(' ');
		}
		return buffer.toString();
	}


	public void clearCounts() {
		for (Counter count : counters.values()) {
			count.reset();
		}
	}


	public void countAnimal(Class<?> animalClass) {
		Counter count = counters.get(animalClass);
		if (count == null) {
			count = new Counter(animalClass.getName());
			counters.put(animalClass, count);
		}
		count.increment();
	}


	public boolean isViable() {
		int nonZero = 0;
		for (Counter info : counters.values()) {
			if (info.getCount() > 0) {
				nonZero++;
			}
		}
		return nonZero > 1;
	}


	public void countField(Field field) {
		clearCounts();
		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				Animal animal = field.getAnimalAt(row, col);
				if (animal != null) {
					countAnimal(animal.getClass());
				}
			}
		}
	}


	public int getPopulationCount(Class<?> key) {
		Counter counter = counters.get(key);
		if (counter == null) {
			return 0;
		}
		return counter.getCount();
	}
}
