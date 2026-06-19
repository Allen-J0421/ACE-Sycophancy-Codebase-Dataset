import java.util.List;
import java.util.Random;


public abstract class Predator extends Animal {


	private static final Random rand = Randomizer.getRandom();

	private boolean cannibal;

	private int additionalFoodValue;


	public Predator(boolean randomAge, Field field, Location location, AnimalSpecies species) {
		super(field, location, species);
		if (randomAge) {
			setAge(rand.nextInt(getMaxAge()));
			setFoodLevel(rand.nextInt(5));
		} else {
			setAge(0);
			setFoodLevel(6);
		}
		cannibal = species.isCannibal();
		additionalFoodValue = species.getAdditionalFoodValue();
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
		List<Location> adjacent = field.adjacentAnimalLocations(getLocation());
		for (Location where : adjacent) {
			Animal nearAnimal = field.getOccupantAt(where, OccupancyLayer.ANIMAL);
			if (nearAnimal != null) {
				if (nearAnimal.getFoodChainLevel() < this.getFoodChainLevel()) {
					if (nearAnimal.isAlive()) {
						nearAnimal.setDead();
						setFoodLevel(nearAnimal.getFoodValue() + additionalFoodValue);
						return where;
					}
				}

				if (nearAnimal.getFoodChainLevel() == this.getFoodChainLevel()) {
					if (nearAnimal.isAlive() && this.isCannibal()) {
						if (getFoodLevel() < 2 && nearAnimal.getClass().equals(this.getClass())) {
							nearAnimal.setDead();
							setFoodLevel(nearAnimal.getFoodValue() + additionalFoodValue);
							return where;
						}
					}
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

	protected int getAdditionalFoodValue() {
		return additionalFoodValue;
	}


	protected void setAdditionalFoodValue(int inputValue) {
		additionalFoodValue = inputValue;
	}
}
