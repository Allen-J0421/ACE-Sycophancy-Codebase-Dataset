import java.util.List;


public abstract class Prey extends Animal {

	private static final RandomService RANDOM = RandomService.shared();


	public Prey(boolean randomAge, EntityController controller, Location location, AnimalSpecies species) {
		super(controller, location, species);
		setAge(0);
		setFoodLevel(6);
		if (randomAge) {
			setAge(RANDOM.nextInt(getSpecies().getMaxAge()));
		}
	}


	protected void normalAct(List<Animal> newAnimals) {
		findFood();
		giveBirth(newAnimals);
		moveTo(getField().freeAnimalAdjacentLocation(getLocation()));
	}


	private void findFood() {
		FieldEnvironment field = getField();
		Location where = getLocation();
		Plant nearPlant = field.getPlantAt(where);

		if (nearPlant != null && nearPlant.canEat()) {
			nearPlant.reduceStage();
			setFoodLevel(8);
		}
	}
}
