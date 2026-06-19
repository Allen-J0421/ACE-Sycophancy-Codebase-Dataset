import java.util.List;
import java.util.Random;


public abstract class Prey extends Animal {

	private static final Random rand = Randomizer.getRandom();

	private static final int DEFAULT_INITIAL_FOOD_LEVEL = 6;

	private static final int PLANT_FOOD_LEVEL = 8;

	private static final int MAX_SICK_STEPS = 20;


	public Prey(boolean randomAge, Field field, Location location, AnimalTraits traits) {
		super(field, location, traits);
		setAge(0);
		setFoodLevel(DEFAULT_INITIAL_FOOD_LEVEL);
		if (randomAge) {
			setAge(rand.nextInt(getMaxAge()));
		}
		setMaxSickStep(MAX_SICK_STEPS);
	}


	protected void normalAct(List<Animal> newAnimals) {
		eatPlantAtCurrentLocation();
		giveBirth(newAnimals);

		Location newLocation = getField().freeAnimalAdjacentLocation(getLocation());
		moveOrDie(newLocation);
	}


	private void eatPlantAtCurrentLocation() {
		Field field = getField();
		Location where = getLocation();
		Plant nearPlant = field.getPlantAt(where);

		if (nearPlant != null) {
			if (nearPlant.canEat()) {
				nearPlant.reduceStage();
				setFoodLevel(PLANT_FOOD_LEVEL);
			}
		}
	}
}
