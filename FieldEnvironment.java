import java.util.List;


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
}
