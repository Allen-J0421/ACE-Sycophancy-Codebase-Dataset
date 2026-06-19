import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class Field {

	private static final Random rand = Randomizer.getRandom();


	private final int depth;
	private final int width;

	private final Animal[][] animalField;

	private final Plant[][] plantField;


	public Field(int depth, int width) {
		this.depth = depth;
		this.width = width;
		animalField = new Animal[depth][width];
		plantField = new Plant[depth][width];
	}


	public void clear() {
		for (int row = 0; row < depth; row++) {
			for (int col = 0; col < width; col++) {
				animalField[row][col] = null;
				plantField[row][col] = null;
			}
		}
	}


	public void clearAnimalAt(Location location) {
		animalField[location.getRow()][location.getCol()] = null;
	}


	public void clearPlantAt(Location location) {
		plantField[location.getRow()][location.getCol()] = null;
	}


	public void placeAnimal(Animal animal, Location location) {
		animalField[location.getRow()][location.getCol()] = animal;
	}


	public void placePlant(Plant plant, Location location) {
		plantField[location.getRow()][location.getCol()] = plant;
	}


	public Animal getAnimalAt(Location location) {
		return getAnimalAt(location.getRow(), location.getCol());
	}


	public Animal getAnimalAt(int row, int col) {
		return animalField[row][col];
	}


	public Plant getPlantAt(Location location) {
		return getPlantAt(location.getRow(), location.getCol());
	}


	public Plant getPlantAt(int row, int col) {
		return plantField[row][col];
	}


	public Location randomAnimalAdjacentLocation(Location location) {
		List<Location> adjacent = getAdjacentAnimalLocations(location);
		return adjacent.get(0);
	}


	public List<Location> getFreeAnimalAdjacentLocations(Location location) {
		List<Location> free = new LinkedList<>();
		List<Location> adjacent = getAdjacentAnimalLocations(location);
		for (Location next : adjacent) {
			if (getAnimalAt(next) == null) {
				free.add(next);
			}
		}
		return free;
	}


	public Location freeAnimalAdjacentLocation(Location location) {

		List<Location> free = getFreeAnimalAdjacentLocations(location);
		if (!free.isEmpty()) {
			return free.get(0);
		} else {
			return null;
		}
	}


	public List<Location> getAdjacentAnimalLocations(Location location) {
		assert location != null : "Null location passed to getAdjacentAnimalLocations";

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
