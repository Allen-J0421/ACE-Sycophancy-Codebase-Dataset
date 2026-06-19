import java.util.List;
import java.util.Random;


public abstract class Animal extends Entity {


	private static final Random rand = Randomizer.getRandom();

	private static final int SICKNESS_RANDOM_TRIGGER = 1;

	private boolean alive;

	private int age;

	private Gender gender = Gender.MALE;

	private boolean nocturnal;

	private int foodLevel;

	private boolean sick;

	private int sickStep;

	private int maxSickStep;

	private final AnimalTraits traits;


	public Animal(Field field, Location location, AnimalTraits traits) {
		super(field, location);
		this.traits = traits;
		alive = true;
		gender = Gender.random();
		nocturnal = false;
		sick = false;
	}


	abstract protected void normalAct(List<Animal> newAnimals);


	public void act(List<Animal> newAnimals, TimeCycle time) {
		incrementAge();
		incrementHunger();
		battleSickness();
		if (isAlive() && canActDuring(time)) {
			normalAct(newAnimals);
		}
	}


	private boolean canActDuring(TimeCycle time) {
		return time == TimeCycle.DAY || isNocturnal();
	}


	protected void incrementAge() {
		age++;
		if (age > getMaxAge()) {
			setDead();
		}
	}


	private boolean canBreed() {
		Field field = getField();
		List<Location> adjacent = field.adjacentAnimalLocations(getLocation());
		boolean oldEnough = age >= getBreedingAge();
		for (Location where : adjacent) {
			Animal animalNear = field.getAnimalAt(where);
			if (isPotentialMate(animalNear)) {
				return oldEnough;
			}
		}
		return oldEnough;
	}


	private boolean isPotentialMate(Animal animal) {
		return animal != null
				&& animal.getClass().equals(getClass())
				&& getGender() != animal.getGender();
	}


	private int breed() {
		int births = 0;
		if (canBreed() && rand.nextDouble() <= getBreedingProbability()) {
			births = rand.nextInt(getMaxLitterSize()) + 1;
		}
		return births;
	}


	protected void giveBirth(List<Animal> newAnimals) {
		if (this.getGender() == Gender.FEMALE) {
			Field field = getField();
			List<Location> free = field.getFreeAnimalAdjacentLocations(getLocation());
			int births = breed();
			for (int b = 0; b < births && free.size() > 0; b++) {
				Location loc = free.remove(0);
				Animal young = createNewAnimal(false, field, loc);
				newAnimals.add(young);
			}
		}
	}


	protected boolean isAlive() {
		return alive;
	}


	protected void setDead() {
		alive = false;
		if (getLocation() != null) {
			getField().clear(getLocation());
			setLocationNull();
			setFieldNull();
		}
	}


	protected void moveOrDie(Location newLocation) {
		if (newLocation != null) {
			setLocation(newLocation);
		} else {
			setDead();
		}
	}


	protected void becomeSick() {
		if (!isSick()) {
			int randomNumber = rand.nextInt(getSickProbability());
			if (randomNumber == SICKNESS_RANDOM_TRIGGER) {
				toggleSick();
			}
		}
	}


	protected void notSick() {
		if (isSick()) {
			int randomNumber = rand.nextInt(getRecoverProbability());
			if (randomNumber == SICKNESS_RANDOM_TRIGGER) {
				toggleSick();
				sickStep = 0;
			}
		}
	}


	protected void battleSickness() {
		if (!sick) {
			becomeSick();
			return;
		}

		if (sickStep >= maxSickStep) {
			setDead();
			return;
		}

		sickStep++;
		Field field = getField();
		if (field != null) {
			spreadSickness(field);
			notSick();
		}
	}


	private void spreadSickness(Field field) {
		List<Location> adjacent = field.adjacentAnimalLocations(getLocation());
		for (Location where : adjacent) {
			Animal nearAnimal = field.getAnimalAt(where);
			if (canInfect(nearAnimal)) {
				nearAnimal.becomeSick();
			}
		}
	}


	private boolean canInfect(Animal animal) {
		return animal != null && animal.getClass().equals(getClass());
	}


	protected void incrementHunger() {
		foodLevel--;
		if (foodLevel <= 0) {
			setDead();
		}
	}


	protected void toggleNocturnal() {
		nocturnal = !nocturnal;

	}


	protected void toggleSick() {
		sick = !sick;
	}


	protected int getSickProbability() {
		return traits.getSickProbability();
	}


	protected int getRecoverProbability() {
		return traits.getRecoverProbability();
	}


	protected Gender getGender() {
		return gender;
	}


	protected int getFoodChainLevel() {
		return traits.getFoodChainLevel();
	}


	protected int getFoodValue() {
		return traits.getFoodValue();
	}


	protected boolean isNocturnal() {
		return nocturnal;
	}


	protected boolean isSick() {
		return sick;
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


	public int getMaxSickStep() {
		return maxSickStep;
	}


	public void setMaxSickStep(int inputValue) {
		this.maxSickStep = inputValue;
	}


	protected int getBreedingAge() {
		return traits.getBreedingAge();
	}


	protected int getMaxAge() {
		return traits.getMaxAge();
	}


	protected double getBreedingProbability() {
		return traits.getBreedingProbability();
	}


	protected int getMaxLitterSize() {
		return traits.getMaxLitterSize();
	}


	abstract protected Animal createNewAnimal(boolean randomAge, Field field, Location loc);
}
