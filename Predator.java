import java.util.List;
import java.util.Random;


public abstract class Predator extends Animal {


	private static final Random rand = Randomizer.getRandom();

	private boolean cannibal;

	private int additionalFoodValue;


	public Predator(boolean randomAge, Field field, Location location) {
		super(field, location);
		if (randomAge) {
			setAge(rand.nextInt(getMaxAge()));
			setFoodLevel(rand.nextInt(5));
		} else {
			setAge(0);
			setFoodLevel(6);
		}
		cannibal = false;
		additionalFoodValue = 0;
		setMaxSickStep(30);
	}


	protected void normalAct(List<Animal> newAnimals) {
		giveBirth(newAnimals);

		Location newLocation = findFood();
		if (isAlive()) {
			if (newLocation == null) {

				newLocation = getField().freeAnimalAdjacentLocation(getLocation());
			}

			if (newLocation != null) {
				setLocation(newLocation);
			} else {

				setDead();
			}
		}
	}


	private Location findFood() {
		Field field = getField();
		for (Location where : field.adjacentAnimalLocations(getLocation())) {
			Object animal = field.getAnimalAt(where);
			if (animal instanceof Animal) {
				Animal nearAnimal = (Animal) animal;

				if (nearAnimal.getFoodChainLevel() < getFoodChainLevel() && nearAnimal.isAlive()) {
					nearAnimal.setDead();
					setFoodLevel(nearAnimal.getFoodValue() + additionalFoodValue);
					return where;
				}

				if (nearAnimal.getFoodChainLevel() == getFoodChainLevel()
						&& nearAnimal.isAlive() && isCannibal()
						&& getFoodLevel() < 2 && nearAnimal.getClass() == this.getClass()) {
					nearAnimal.setDead();
					setFoodLevel(nearAnimal.getFoodValue() + additionalFoodValue);
					return where;
				}
			}
		}
		return null;
	}


	public boolean isCannibal() {
		return cannibal;
	}


	public void toggleCannibal() {
		cannibal = !cannibal;
	}


	protected void setAdditionalFoodValue(int inputValue) {
		additionalFoodValue = inputValue;
	}
}
