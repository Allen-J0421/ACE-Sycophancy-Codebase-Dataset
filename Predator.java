import java.util.Iterator;
import java.util.List;
import java.util.Random;


public abstract class Predator extends Animal {


	private static final Random rand = Randomizer.getRandom();

	public Predator(boolean randomAge, FieldEnvironment field, Location location, AnimalSpecies species) {
		super(field, location, species);
		if (randomAge) {
			setAge(rand.nextInt(getSpecies().getMaxAge()));
			setFoodLevel(rand.nextInt(5));
		} else {
			setAge(0);
			setFoodLevel(6);
		}
	}


	protected void normalAct(List<Animal> newAnimals) {
		giveBirth(newAnimals);

		Location newLocation = findFood();
		if (isAlive()) {
			if (newLocation == null) {
				newLocation = getField().freeAnimalAdjacentLocation(getLocation());
			}
			moveTo(newLocation);
		}
	}


	private Location findFood() {
		FieldEnvironment field = getField();
		List<Location> adjacent = field.getAdjacentAnimalLocations(getLocation());
		Iterator<Location> it = adjacent.iterator();
		while (it.hasNext()) {
			Location where = it.next();
			Animal nearAnimal = field.getAnimalAt(where);
			if (nearAnimal != null) {
				if (nearAnimal.getFoodChainLevel() < this.getFoodChainLevel()) {
					if (nearAnimal.isAlive()) {
						nearAnimal.setDead();
						setFoodLevel(nearAnimal.getFoodValue() + getSpecies().getAdditionalFoodValue());
						return where;
					}
				}

				if (nearAnimal.getFoodChainLevel() == this.getFoodChainLevel()) {
					if (nearAnimal.isAlive() && getSpecies().isCannibal()) {
						if (getFoodLevel() < 2 && nearAnimal.getClass().equals(this.getClass())) {
							nearAnimal.setDead();
							setFoodLevel(nearAnimal.getFoodValue() + getSpecies().getAdditionalFoodValue());
							return where;
						}
					}
				}
			}
		}
		return null;
	}
}
