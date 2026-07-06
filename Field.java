import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class Field {

	private static final Random rand = Randomizer.getRandom();


	private final int depth;

	private final int width;

	private final Grid<Animal> animalGrid = new Grid<>();

	private final Grid<Plant> plantGrid = new Grid<>();


	public Field(int depth, int width) {
		this.depth = depth;
		this.width = width;
	}


	public void clear() {
		animalGrid.clear();
		plantGrid.clear();
	}


	public void place(FieldOccupant occupant, Location location) {
		if (occupant.getOccupantLayer() == FieldOccupant.Layer.ANIMAL) {
			animalGrid.place((Animal) occupant, location);
		} else {
			plantGrid.place((Plant) occupant, location);
		}
	}


	public void removeOccupant(FieldOccupant occupant, Location location) {
		if (occupant.getOccupantLayer() == FieldOccupant.Layer.ANIMAL) {
			animalGrid.remove(location);
		} else {
			plantGrid.remove(location);
		}
	}


	public Animal getAnimalAt(Location location) {
		return animalGrid.get(location);
	}


	public Animal getAnimalAt(int row, int col) {
		return getAnimalAt(new Location(row, col));
	}


	public Plant getPlantAt(Location location) {
		return plantGrid.get(location);
	}


	public Plant getPlantAt(int row, int col) {
		return getPlantAt(new Location(row, col));
	}


	public Location randomAnimalAdjacentLocation(Location location) {
		List<Location> adjacent = adjacentAnimalLocations(location);
		return adjacent.get(0);
	}


	public List<Location> getFreeAnimalAdjacentLocations(Location location) {
		List<Location> free = new LinkedList<>();
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
