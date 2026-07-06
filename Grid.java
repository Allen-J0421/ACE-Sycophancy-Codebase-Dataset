import java.util.HashMap;
import java.util.Map;


public class Grid<T extends FieldOccupant> {


	private final Map<Location, T> cells = new HashMap<>();


	public void place(T occupant, Location location) {
		cells.put(location, occupant);
	}


	public void remove(Location location) {
		cells.remove(location);
	}


	public T get(Location location) {
		return cells.get(location);
	}


	public boolean isEmpty(Location location) {
		return !cells.containsKey(location);
	}


	public void clear() {
		cells.clear();
	}

}
