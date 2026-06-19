import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Field {

	private static final Random rand = Randomizer.getRandom();

	private final int depth;
	private final int width;
	private final Cell[][] cells;


	public Field(int depth, int width) {
		this.depth = depth;
		this.width = width;
		cells = new Cell[depth][width];
		for (int row = 0; row < depth; row++) {
			for (int col = 0; col < width; col++) {
				cells[row][col] = new Cell();
			}
		}
	}


	public void clear() {
		for (int row = 0; row < depth; row++) {
			for (int col = 0; col < width; col++) {
				cells[row][col].clear();
			}
		}
	}


	public void clear(Location location) {
		cellAt(location).clearAnimal();
	}


	public void placeAnimal(Animal animal, int row, int col) {
		placeAnimal(animal, new Location(row, col));
	}


	public void placeAnimal(Animal animal, Location location) {
		cellAt(location).setAnimal(animal);
	}


	public void placePlant(Plant plant, Location location) {
		cellAt(location).setPlant(plant);
	}


	public Animal getAnimalAt(Location location) {
		return cellAt(location).getAnimal();
	}


	public Animal getAnimalAt(int row, int col) {
		return cellAt(row, col).getAnimal();
	}


	public Plant getPlantAt(Location location) {
		return cellAt(location).getPlant();
	}


	public Plant getPlantAt(int row, int col) {
		return cellAt(row, col).getPlant();
	}


	public Location randomAnimalAdjacentLocation(Location location) {
		List<Location> adjacent = adjacentAnimalLocations(location);
		return adjacent.isEmpty() ? null : adjacent.get(0);
	}


	public List<Location> getFreeAnimalAdjacentLocations(Location location) {
		List<Location> free = new ArrayList<>();
		for (Location next : adjacentAnimalLocations(location)) {
			if (getAnimalAt(next) == null) {
				free.add(next);
			}
		}
		return free;
	}


	public Location freeAnimalAdjacentLocation(Location location) {
		List<Location> free = getFreeAnimalAdjacentLocations(location);
		return free.isEmpty() ? null : free.get(0);
	}


	public List<Location> adjacentAnimalLocations(Location location) {
		assert location != null : "Null location passed to adjacentLocations";

		List<Location> locations = new ArrayList<>();
		if (location != null) {
			int row = location.getRow();
			int col = location.getCol();
			for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
				int nextRow = row + rowOffset;
				if (nextRow >= 0 && nextRow < depth) {
					for (int colOffset = -1; colOffset <= 1; colOffset++) {
						int nextCol = col + colOffset;
						if (nextCol >= 0 && nextCol < width && (rowOffset != 0 || colOffset != 0)) {
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


	private Cell cellAt(Location location) {
		return cells[location.getRow()][location.getCol()];
	}


	private Cell cellAt(int row, int col) {
		return cells[row][col];
	}


	private static final class Cell {
		private Animal animal;
		private Plant plant;


		private void clear() {
			animal = null;
			plant = null;
		}


		private void clearAnimal() {
			animal = null;
		}


		private Animal getAnimal() {
			return animal;
		}


		private Plant getPlant() {
			return plant;
		}


		private void setAnimal(Animal animal) {
			this.animal = animal;
		}


		private void setPlant(Plant plant) {
			this.plant = plant;
		}
	}
}
