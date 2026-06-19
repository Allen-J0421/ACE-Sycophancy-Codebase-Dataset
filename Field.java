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


	public void place(Movable movable, int row, int col) {
		place(movable, new Location(row, col));
	}


	public void place(Movable movable, Location location) {
		cellAt(location).set(movable);
	}


	public void updateOccupancy(Movable movable, Location from, Location to) {
		if (from != null) {
			cellAt(from).clear(movable.getOccupancyLayer());
		}
		if (to != null) {
			place(movable, to);
		}
	}


	public <T extends Movable> T getOccupantAt(Location location, OccupancyLayer layer) {
		return cellAt(location).get(layer);
	}


	public <T extends Movable> T getOccupantAt(int row, int col, OccupancyLayer layer) {
		return cellAt(row, col).get(layer);
	}


	public Location randomAnimalAdjacentLocation(Location location) {
		List<Location> adjacent = adjacentAnimalLocations(location);
		return adjacent.isEmpty() ? null : adjacent.get(0);
	}


	public List<Location> getFreeAnimalAdjacentLocations(Location location) {
		List<Location> free = new ArrayList<>();
		for (Location next : adjacentAnimalLocations(location)) {
			if (getOccupantAt(next, OccupancyLayer.ANIMAL) == null) {
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


		private void clear(OccupancyLayer layer) {
			switch (layer) {
				case ANIMAL:
					animal = null;
					break;
				case PLANT:
					plant = null;
					break;
				default:
					throw new IllegalStateException("Unhandled layer: " + layer);
			}
		}


		private void set(Movable occupant) {
			switch (occupant.getOccupancyLayer()) {
				case ANIMAL:
					animal = (Animal) occupant;
					break;
				case PLANT:
					plant = (Plant) occupant;
					break;
				default:
					throw new IllegalStateException("Unhandled layer: " + occupant.getOccupancyLayer());
			}
		}


		@SuppressWarnings("unchecked")
		private <T extends Movable> T get(OccupancyLayer layer) {
			switch (layer) {
				case ANIMAL:
					return (T) animal;
				case PLANT:
					return (T) plant;
				default:
					throw new IllegalStateException("Unhandled layer: " + layer);
			}
		}
	}
}
