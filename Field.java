import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class Field {

	private static final Random rand = Randomizer.getRandom();


	private final int depth;

	private final int width;

	private final EnumMap<FieldOccupant.Layer, Grid> layers;


	public Field(int depth, int width) {
		this.depth = depth;
		this.width = width;
		layers = new EnumMap<>(FieldOccupant.Layer.class);
		for (FieldOccupant.Layer layer : FieldOccupant.Layer.values()) {
			layers.put(layer, new Grid());
		}
	}


	public void clear() {
		for (Grid grid : layers.values()) {
			grid.clear();
		}
	}


	public void place(FieldOccupant occupant, Location location) {
		gridFor(occupant).place(occupant, location);
	}


	public void removeOccupant(FieldOccupant occupant, Location location) {
		gridFor(occupant).remove(location);
	}


	private Grid gridFor(FieldOccupant occupant) {
		return layers.get(occupant.getOccupantLayer());
	}


	public Object getAnimalAt(Location location) {
		return layers.get(FieldOccupant.Layer.ANIMAL).get(location);
	}


	public Object getAnimalAt(int row, int col) {
		return getAnimalAt(new Location(row, col));
	}


	public Object getPlantAt(Location location) {
		return layers.get(FieldOccupant.Layer.PLANT).get(location);
	}


	public Object getPlantAt(int row, int col) {
		return getPlantAt(new Location(row, col));
	}


	public Location randomAnimalAdjacentLocation(Location location) {
		List<Location> adjacent = adjacentAnimalLocations(location);
		return adjacent.get(0);
	}


	public List<Location> getFreeAnimalAdjacentLocations(Location location) {
		List<Location> free = new LinkedList<>();
		Grid animalGrid = layers.get(FieldOccupant.Layer.ANIMAL);
		for (Location next : adjacentAnimalLocations(location)) {
			if (animalGrid.isEmpty(next)) {
				free.add(next);
			}
		}
		return free;
	}


	public Location freeAnimalAdjacentLocation(Location location) {
		List<Location> free = getFreeAnimalAdjacentLocations(location);
		if (free.size() > 0) {
			return free.get(0);
		} else {
			return null;
		}
	}


	public List<Location> adjacentAnimalLocations(Location location) {
		assert location != null : "Null location passed to adjacentLocations";

		List<Location> locations = new LinkedList<>();
		if (location != null) {
			int row = location.getRow();
			int col = location.getCol();
			for (int roffset = -1; roffset <= 1; roffset++) {
				int nextRow = row + roffset;
				if (nextRow >= 0 && nextRow < depth) {
					for (int coffset = -1; coffset <= 1; coffset++) {
						int nextCol = col + coffset;

						if (nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
							locations.add(new Location(nextRow, nextCol));
						}
					}
				}
			}


			Collections.shuffle(locations, rand);
		}
		return locations;
	}


	public int getDepth() {
		return depth;
	}


	public int getWidth() {
		return width;
	}
}
