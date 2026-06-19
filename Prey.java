import java.util.List;
import java.util.Random;


public abstract class Prey extends Animal {

	private static final Random rand = Randomizer.getRandom();


	public Prey(AnimalTraits traits, boolean randomAge, Field field, Location location) {
		super(traits, field, location);
		setAge(0);
		setFoodLevel(6);
		if (randomAge) {
			setAge(rand.nextInt(getMaxAge()));
		}
	}


	protected void normalAct(List<Animal> newAnimals) {
		findFood();
		giveBirth(newAnimals);

		Location newLocation = getField().freeAnimalAdjacentLocation(getLocation());
		if (newLocation != null) {
			setLocation(newLocation);
		} else {

			setDead();
		}
	}


	private void findFood() {
		Plant plant = getField().getPlantAt(getLocation());
		if (plant != null && plant.canEat()) {
			plant.reduceStage();
			setFoodLevel(8);
		}
	}
}
