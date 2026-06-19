import java.util.List;
import java.util.Random;


public abstract class Prey extends Animal {

	private static final Random rand = Randomizer.getRandom();


	public Prey(boolean randomAge, FieldEnvironment field, Location location, AnimalSpecies species) {
		super(field, location, species);
		setAge(0);
		setFoodLevel(6);
		if (randomAge) {
			setAge(rand.nextInt(getSpecies().getMaxAge()));
		}
	}


	protected void normalAct(List<Animal> newAnimals) {
		findFood();
		giveBirth(newAnimals);
		moveTo(getField().freeAnimalAdjacentLocation(getLocation()));
	}


	private void findFood() {
		FieldEnvironment field = getField();
		Location where = getLocation();
		Plant nearPlant = field.getPlantAt(where);

		if (nearPlant != null && nearPlant.canEat()) {
			nearPlant.reduceStage();
			setFoodLevel(8);
		}
	}
}
