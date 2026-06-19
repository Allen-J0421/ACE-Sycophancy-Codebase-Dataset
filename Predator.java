import java.util.List;
import java.util.Random;


public abstract class Predator extends Animal {


	private static final Random rand = Randomizer.getRandom();

	private static final int RANDOM_INITIAL_FOOD_BOUND = 5;

	private static final int DEFAULT_INITIAL_FOOD_LEVEL = 6;

	private static final int CANNIBAL_HUNGER_THRESHOLD = 2;

	private static final int MAX_SICK_STEPS = 30;

	private boolean cannibal;

	private int additionalFoodValue;


	public Predator(boolean randomAge, Field field, Location location, AnimalTraits traits) {
		super(field, location, traits);
		if (randomAge) {
			setAge(rand.nextInt(getMaxAge()));
			setFoodLevel(rand.nextInt(RANDOM_INITIAL_FOOD_BOUND));
		} else {
			setAge(0);
			setFoodLevel(DEFAULT_INITIAL_FOOD_LEVEL);
		}
		cannibal = false;
		additionalFoodValue = 0;
		setMaxSickStep(MAX_SICK_STEPS);
	}


	protected void normalAct(List<Animal> newAnimals) {
		giveBirth(newAnimals);

		Location newLocation = findFood();
		if (isAlive()) {
			if (newLocation == null) {
				newLocation = getField().freeAnimalAdjacentLocation(getLocation());
			}
			moveOrDie(newLocation);
		}
	}


	private Location findFood() {
		Field field = getField();
		List<Location> adjacent = field.adjacentAnimalLocations(getLocation());
		for (Location where : adjacent) {
			Animal nearAnimal = field.getAnimalAt(where);
			if (canEat(nearAnimal)) {
				eat(nearAnimal);
				return where;
			}
		}
		return null;
	}


	private boolean canEat(Animal animal) {
		if (animal == null || !animal.isAlive()) {
			return false;
		}
		if (animal.getFoodChainLevel() < getFoodChainLevel()) {
			return true;
		}
		return canCannibalize(animal);
	}


	private boolean canCannibalize(Animal animal) {
		return animal.getFoodChainLevel() == getFoodChainLevel()
				&& isCannibal()
				&& getFoodLevel() < CANNIBAL_HUNGER_THRESHOLD
				&& animal.getClass().equals(getClass());
	}


	private void eat(Animal animal) {
		animal.setDead();
		setFoodLevel(animal.getFoodValue() + additionalFoodValue);
	}


	public boolean isCannibal() {
		return cannibal;
	}


	public void toggleCannibal() {
		cannibal = !cannibal;
	}


	protected int getAdditionalFoodValue() {
		return additionalFoodValue;
	}


	protected void setAdditionalFoodValue(int inputValue) {
		additionalFoodValue = inputValue;
	}
}
