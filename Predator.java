import java.util.List;


public abstract class Predator extends Animal {


	private static final RandomService RANDOM = RandomService.shared();

	public Predator(boolean randomAge, EntityController controller, Location location, AnimalSpecies species) {
		super(controller, location, species);
		if (randomAge) {
			setAge(RANDOM.nextInt(getSpecies().getMaxAge()));
			setFoodLevel(RANDOM.nextInt(5));
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
		return getField().getAdjacentAnimalLocations(getLocation()).stream()
				.filter(this::canEatAt)
				.findFirst()
				.map(this::eatAnimalAt)
				.orElse(null);
	}


	private boolean canEatAt(Location location) {
		Animal nearAnimal = getField().getAnimalAt(location);
		return nearAnimal != null && nearAnimal.isAlive()
				&& (nearAnimal.getFoodChainLevel() < getFoodChainLevel() || canEatSameSpecies(nearAnimal));
	}


	private boolean canEatSameSpecies(Animal nearAnimal) {
		return getSpecies().isCannibal()
				&& nearAnimal.getFoodChainLevel() == getFoodChainLevel()
				&& getFoodLevel() < 2
				&& nearAnimal.getClass().equals(getClass());
	}


	private Location eatAnimalAt(Location location) {
		Animal nearAnimal = getField().getAnimalAt(location);
		nearAnimal.setDead();
		setFoodLevel(nearAnimal.getFoodValue() + getSpecies().getAdditionalFoodValue());
		return location;
	}
}
