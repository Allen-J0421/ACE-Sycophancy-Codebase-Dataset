import java.util.Objects;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public interface FieldEnvironment {

	void clear();


	void clearAnimalAt(Location location);


	void clearPlantAt(Location location);


	void placeAnimal(Animal animal, Location location);


	void placePlant(Plant plant, Location location);


	Animal getAnimalAt(Location location);


	Animal getAnimalAt(int row, int col);


	Plant getPlantAt(Location location);


	Plant getPlantAt(int row, int col);


	List<Location> getAdjacentAnimalLocations(Location location);


	List<Location> getFreeAnimalAdjacentLocations(Location location);


	Location freeAnimalAdjacentLocation(Location location);


	int getDepth();


	int getWidth();


	default Stream<Location> streamLocations() {
		return IntStream.range(0, getDepth())
				.boxed()
				.flatMap(row -> IntStream.range(0, getWidth())
						.mapToObj(col -> new Location(row, col)));
	}


	default Stream<Animal> streamAnimals() {
		return streamLocations()
				.map(this::getAnimalAt)
				.filter(Objects::nonNull);
	}


	default Stream<Animal> streamAdjacentAnimals(Location location) {
		return getAdjacentAnimalLocations(location).stream()
				.map(this::getAnimalAt)
				.filter(Objects::nonNull);
	}
}
