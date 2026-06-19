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
		Field field = getField();
		Location where = getLocation();
		Object plant = field.getPlantAt(where);

		if (plant instanceof Plant) {
			Plant nearPlant = (Plant) plant;

			if (nearPlant.canEat()) {
				nearPlant.reduceStage();
				setFoodLevel(8);
			}
		}
	}
}
