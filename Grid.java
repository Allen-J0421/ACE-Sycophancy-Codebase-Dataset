import java.util.HashMap;
import java.util.Map;


public class Grid {


	private final Map<Location, FieldOccupant> cells = new HashMap<>();


	public void place(FieldOccupant occupant, Location location) {
		cells.put(location, occupant);
	}


	public void remove(Location location) {
		cells.remove(location);
	}


	public FieldOccupant get(Location location) {
		return cells.get(location);
	}


	public boolean isEmpty(Location location) {
		return !cells.containsKey(location);
	}


	public void clear() {
		cells.clear();
	}

}
