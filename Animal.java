import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public abstract class Animal extends Entity {


	private static final RandomService RANDOM = RandomService.shared();

	private final AnimalSpecies species;

	private boolean alive;

	private int age;

	private final Gender gender;

	private int foodLevel;

	private boolean sick;

	private int sickStep;


	public Animal(FieldEnvironment field, Location location, AnimalSpecies species) {
		super(field, location);
		this.species = species;
		alive = true;
		gender = Gender.randomGender();
		sick = false;
	}


	abstract protected void normalAct(List<Animal> newAnimals);


	public void act(List<Animal> newAnimals, TimeCycle time) {
		incrementAge();
		incrementHunger();
		battleSickness();
		if (isAlive() && isActiveDuring(time)) {
			normalAct(newAnimals);
		}
	}


	protected void incrementAge() {
		age++;
		if (age > getMaxAge()) {
			setDead();
		}
	}


	private boolean canBreed() {
		return age >= getBreedingAge()
				&& getField().streamAdjacentAnimals(getLocation())
				.anyMatch(animalNear -> animalNear.getClass().equals(getClass())
						&& getGender() != animalNear.getGender());
	}


	private int breed() {
		int births = 0;
		if (canBreed() && RANDOM.chance(getBreedingProbability())) {
			births = RANDOM.nextIntInclusive(1, getMaxLitterSize());
		}
		return births;
	}


	protected void giveBirth(List<Animal> newAnimals) {


		if (this.getGender() == Gender.FEMALE) {
			FieldEnvironment field = getField();
			List<Location> free = field.getFreeAnimalAdjacentLocations(getLocation());
			int births = breed();
			newAnimals.addAll(IntStream.range(0, Math.min(births, free.size()))
					.mapToObj(free::get)
					.map(location -> species.createAnimal(false, field, location))
					.collect(Collectors.toList()));
		}
	}


	protected boolean isAlive() {
		return alive;
	}


	protected void setDead() {
		alive = false;
		if (getLocation() != null) {
			clearFieldLocation(getField(), getLocation());
			setLocationNull();
			setFieldNull();
		}
	}


	protected void becomeSick() {
		if (!isSick() && RANDOM.oneIn(getSickProbability())) {
			toggleSick();
		}
	}


	protected void notSick() {
		if (isSick() && RANDOM.oneIn(getRecoverProbability())) {
			toggleSick();
			sickStep = 0;
		}
	}


	protected void battleSickness() {
		if (sick) {
			if (sickStep >= species.getMaxSickStep()) {
				setDead();
				return;
			}
			sickStep++;
			FieldEnvironment field = getField();
			if (field != null) {
				field.streamAdjacentAnimals(getLocation())
						.filter(nearAnimal -> nearAnimal.getClass().equals(getClass()))
						.forEach(Animal::becomeSick);
				this.notSick();
			}
		} else {
			becomeSick();
		}
	}


	protected void incrementHunger() {
		foodLevel--;
		if (foodLevel <= 0) {
			setDead();
		}
	}


	protected void moveTo(Location newLocation) {
		if (newLocation != null) {
			setLocation(newLocation);
		} else {
			setDead();
		}
	}


	protected void toggleSick() {
		sick = !sick;

	}


	protected int getSickProbability() {
		return species.getSickProbability();
	}


	protected int getRecoverProbability() {
		return species.getRecoverProbability();
	}


	protected Gender getGender() {
		return gender;
	}


	protected int getFoodChainLevel() {
		return species.getFoodChainLevel();
	}


	protected int getFoodValue() {
		return species.getFoodValue();
	}


	protected boolean isNocturnal() {
		return species.isNocturnal();
	}


	protected boolean isSick() {
		return sick;
	}


	@Override
	protected void placeInField(FieldEnvironment field, Location location) {
		field.placeAnimal(this, location);
	}


	@Override
	protected void clearFieldLocation(FieldEnvironment field, Location location) {
		field.clearAnimalAt(location);
	}


	protected void setAge(int age) {
		this.age = age;
	}


	public int getFoodLevel() {
		return foodLevel;
	}


	public void setFoodLevel(int foodLevel) {
		this.foodLevel = foodLevel;
	}


	public int getSickStep() {
		return sickStep;
	}


	public void setSickStep(int inputValue) {
		this.sickStep = inputValue;
	}


	protected AnimalSpecies getSpecies() {
		return species;
	}


	private boolean isActiveDuring(TimeCycle time) {
		return time == TimeCycle.NIGHT ? isNocturnal() : !isNocturnal();
	}


	private int getBreedingAge() {
		return species.getBreedingAge();
	}


	private int getMaxAge() {
		return species.getMaxAge();
	}


	private double getBreedingProbability() {
		return species.getBreedingProbability();
	}


	private int getMaxLitterSize() {
		return species.getMaxLitterSize();
	}
}
