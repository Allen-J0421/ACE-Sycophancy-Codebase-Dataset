import java.util.List;


public class StandardBreedingStrategy implements BreedingStrategy {


	public void breed(Animal animal, List<Animal> newAnimals) {
		animal.giveBirth(newAnimals);
	}

}
