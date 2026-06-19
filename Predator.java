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
		setMaxSickStep(30);
	}


	@Override
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
			Animal nearAnimal = field.getAnimalAt(where);
			if (nearAnimal != null) {
				if (nearAnimal.getFoodChainLevel() < getFoodChainLevel() && nearAnimal.isAlive()) {
					nearAnimal.setDead();
					setFoodLevel(nearAnimal.getFoodValue() + additionalFoodValue);
					return where;
				}

				if (nearAnimal.getFoodChainLevel() == getFoodChainLevel()
						&& nearAnimal.isAlive() && isCannibal()
						&& getFoodLevel() < 2 && nearAnimal.getClass() == getClass()) {
					nearAnimal.setDead();
					setFoodLevel(nearAnimal.getFoodValue() + additionalFoodValue);
					return where;
				}
			}
		}
		return null;
	}


	protected boolean isCannibal() {
		return cannibal;
	}


	protected void setCannibal(boolean value) {
		cannibal = value;
	}


	protected void setAdditionalFoodValue(int inputValue) {
		additionalFoodValue = inputValue;
	}
}
