import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * A rectangular grid that holds the animals and plants of the simulation.
 * Animals and plants live on two separate, overlaid layers so a cell can
 * contain one of each at the same time.
 */
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


	/** Remove every animal and plant from the field. */
	public void clear() {
		for (int row = 0; row < depth; row++) {
			for (int col = 0; col < width; col++) {
				animalField[row][col] = null;
				plantField[row][col] = null;
			}
		}
	}


	/** Remove the animal (if any) at the given location. */
	public void clear(Location location) {
		animalField[location.getRow()][location.getCol()] = null;
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


	/** Adjacent locations that currently hold no animal. */
	public List<Location> getFreeAnimalAdjacentLocations(Location location) {
		List<Location> free = new LinkedList<>();
		for (Location next : adjacentAnimalLocations(location)) {
			if (getAnimalAt(next) == null) {
				free.add(next);
			}
		}
		return free;
	}


	/** A single free adjacent location, or {@code null} if the cell is hemmed in. */
	public Location freeAnimalAdjacentLocation(Location location) {
		List<Location> free = getFreeAnimalAdjacentLocations(location);
		return free.isEmpty() ? null : free.get(0);
	}


	/** The (shuffled) in-bounds neighbours of a location, excluding itself. */
	public List<Location> adjacentAnimalLocations(Location location) {
		assert location != null : "Null location passed to adjacentLocations";

		List<Location> locations = new LinkedList<>();
		int row = location.getRow();
		int col = location.getCol();
		for (int roffset = -1; roffset <= 1; roffset++) {
			int nextRow = row + roffset;
			if (nextRow < 0 || nextRow >= depth) {
				continue;
			}
			for (int coffset = -1; coffset <= 1; coffset++) {
				int nextCol = col + coffset;
				boolean inBounds = nextCol >= 0 && nextCol < width;
				boolean isSelf = roffset == 0 && coffset == 0;
				if (inBounds && !isSelf) {
					locations.add(new Location(nextRow, nextCol));
				}
			}
		}

		Collections.shuffle(locations, rand);
		return locations;
	}


	public int getDepth() {
		return depth;
	}


	public int getWidth() {
		return width;
	}
}
