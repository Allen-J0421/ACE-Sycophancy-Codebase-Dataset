import java.util.List;
import java.util.Random;


public abstract class Animal extends Entity {


	private static final Random rand = Randomizer.getRandom();

	/** Default odds of catching an illness, before a species tunes them. */
	private static final int DEFAULT_INFECTION_RESISTANCE = 16;

	private boolean alive;

	private int age;

	private Gender gender;

	private boolean nocturnal;

	private int foodChainLevel;

	private int foodValue;

	private int foodLevel;

	private final Disease disease = new Disease(DEFAULT_INFECTION_RESISTANCE);


	public Animal(Field field, Location location) {
		super(field, location);
		alive = true;
		gender = Gender.random();
	}


	abstract protected void normalAct(List<Animal> newAnimals);


	public void act(List<Animal> newAnimals, TimeCycle time) {
		incrementAge();
		incrementHunger();
		battleSickness();
		// Nocturnal animals act around the clock; the rest only act during the day.
		if (isAlive() && (isNocturnal() || time == TimeCycle.DAY)) {
			normalAct(newAnimals);
		}
	}


	@Override
	protected void placeInField(Field field, Location location) {
		field.placeAnimal(this, location);
	}


	protected void incrementAge() {
		age++;
		if (age > getMaxAge()) {
			setDead();
		}
	}


	private boolean canBreed() {
		return age >= getBreedingAge();
	}


	private int breed() {
		int births = 0;
		if (canBreed() && rand.nextDouble() <= getBreedingProbability()) {
			births = rand.nextInt(getMaxLitterSize()) + 1;
		}
		return births;
	}


	protected void giveBirth(List<Animal> newAnimals) {
		if (getGender() != Gender.FEMALE) {
			return;
		}
		Field field = getField();
		List<Location> free = field.getFreeAnimalAdjacentLocations(getLocation());
		int births = breed();
		for (int b = 0; b < births && !free.isEmpty(); b++) {
			Location loc = free.remove(0);
			newAnimals.add(createNewAnimal(false, field, loc));
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


	protected void battleSickness() {
		if (!disease.isActive()) {
			disease.tryInfect();
			return;
		}

		if (disease.isTerminal()) {
			setDead();
			return;
		}
		disease.advance();

		Field field = getField();
		if (field == null) {
			return;
		}
		// Spread the illness to same-species neighbours, then try to recover.
		for (Location where : field.adjacentAnimalLocations(getLocation())) {
			Animal neighbour = field.getAnimalAt(where);
			if (neighbour != null && neighbour.getClass().equals(getClass())) {
				neighbour.disease.tryInfect();
			}
		}
		disease.tryRecover();
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


	protected void setSickProbability(int inputValue) {
		disease.setInfectionResistance(inputValue);
	}


	protected void setRecoverProbability(int inputValue) {
		disease.setRecoveryResistance(inputValue);
	}


	protected Gender getGender() {
		return gender;
	}


	protected int getFoodChainLevel() {
		return foodChainLevel;
	}


	protected void setFoodChainLevel(int level) {
		foodChainLevel = level;
	}


	protected int getFoodValue() {
		return foodValue;
	}


	protected void setFoodValue(int value) {
		foodValue = value;
	}


	protected boolean isNocturnal() {
		return nocturnal;
	}


	protected boolean isSick() {
		return disease.isActive();
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


	public void setMaxSickStep(int inputValue) {
		disease.setMaxDuration(inputValue);
	}


	abstract protected int getBreedingAge();


	abstract protected int getMaxAge();


	abstract protected double getBreedingProbability();


	abstract protected int getMaxLitterSize();


	abstract protected Animal createNewAnimal(boolean randomAge, Field field, Location loc);
}
