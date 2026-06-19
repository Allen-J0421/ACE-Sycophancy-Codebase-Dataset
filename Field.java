import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Field implements FieldEnvironment {

	private static final RandomService RANDOM = RandomService.shared();


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


	@Override
	public void clear() {
		Arrays.stream(animalField).forEach(row -> Arrays.fill(row, null));
		Arrays.stream(plantField).forEach(row -> Arrays.fill(row, null));
	}


	@Override
	public void clearAnimalAt(Location location) {
		animalField[location.getRow()][location.getCol()] = null;
	}


	@Override
	public void clearPlantAt(Location location) {
		plantField[location.getRow()][location.getCol()] = null;
	}


	@Override
	public void placeAnimal(Animal animal, Location location) {
		animalField[location.getRow()][location.getCol()] = animal;
	}


	@Override
	public void placePlant(Plant plant, Location location) {
		plantField[location.getRow()][location.getCol()] = plant;
	}


	@Override
	public Animal getAnimalAt(Location location) {
		return getAnimalAt(location.getRow(), location.getCol());
	}


	@Override
	public Animal getAnimalAt(int row, int col) {
		return animalField[row][col];
	}


	@Override
	public Plant getPlantAt(Location location) {
		return getPlantAt(location.getRow(), location.getCol());
	}


	@Override
	public Plant getPlantAt(int row, int col) {
		return plantField[row][col];
	}


	@Override
	public List<Location> getFreeAnimalAdjacentLocations(Location location) {
		return getAdjacentAnimalLocations(location).stream()
				.filter(next -> getAnimalAt(next) == null)
				.collect(Collectors.toCollection(ArrayList::new));
	}


	@Override
	public Location freeAnimalAdjacentLocation(Location location) {

		List<Location> free = getFreeAnimalAdjacentLocations(location);
		if (!free.isEmpty()) {
			return free.get(0);
		} else {
			return null;
		}
	}


	@Override
	public List<Location> getAdjacentAnimalLocations(Location location) {
		assert location != null : "Null location passed to getAdjacentAnimalLocations";

		if (location == null) {
			return Collections.emptyList();
		}

		int row = location.getRow();
		int col = location.getCol();
		List<Location> locations = IntStream.rangeClosed(Math.max(0, row - 1), Math.min(depth - 1, row + 1))
				.boxed()
				.flatMap(nextRow -> IntStream.rangeClosed(Math.max(0, col - 1), Math.min(width - 1, col + 1))
						.filter(nextCol -> nextRow != row || nextCol != col)
						.mapToObj(nextCol -> new Location(nextRow, nextCol)))
				.collect(Collectors.toCollection(ArrayList::new));
		RANDOM.shuffle(locations);
		return locations;
	}


	@Override
	public int getDepth() {
		return depth;
	}


	@Override
	public int getWidth() {
		return width;
	}
}
