import java.util.List;
import java.util.Random;


public abstract class Prey extends Animal {

	private static final Random rand = Randomizer.getRandom();

	public Prey(boolean randomAge, Field field, Location location) {
		super(field, location);
		setAge(0);
		setFoodLevel(6);
		if (randomAge) {
			setAge(rand.nextInt(getMaxAge()));
		}
		setMaxSickStep(20);
	}


	@Override
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
		Plant nearPlant = getField().getPlantAt(getLocation());
		if (nearPlant != null && nearPlant.canEat()) {
			nearPlant.reduceStage();
			setFoodLevel(8);
		}
	}
}
