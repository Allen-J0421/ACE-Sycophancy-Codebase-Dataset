import java.util.List;
import java.util.Random;


public abstract class Animal extends Entity {


	private static final Random rand = Randomizer.getRandom();

	private boolean alive;

	private int age;

	private Gender gender = Gender.MALE;

	private boolean nocturnal;

	private int foodChainLevel;

	private int foodValue;

	private int foodLevel;

	private boolean sick;

	private int sickProbability;

	private int recoverProbability;

	private int sickStep;

	private int maxSickStep;


	public Animal(Field field, Location location) {
		super(field, location);
		alive = true;
		gender = gender.randomGender();
		nocturnal = false;
		sick = false;
		sickProbability = 16;
	}


	abstract protected void normalAct(List<Animal> newAnimals);


	public void act(List<Animal> newAnimals, TimeCycle time) {
		incrementAge();
		incrementHunger();
		battleSickness();
		if (isAlive()) {
			boolean shouldAct = (time == TimeCycle.NIGHT && isNocturnal())
					|| (time == TimeCycle.DAY && !isNocturnal());
			if (shouldAct) {
				normalAct(newAnimals);
			}
		}
	}


	protected void incrementAge() {
		age++;
		if (age > getMaxAge()) {
			setDead();
		}
	}


	private boolean canBreed() {
		if (age < getBreedingAge()) {
			return false;
		}
		Field field = getField();
		List<Location> adjacent = field.adjacentAnimalLocations(getLocation());
		for (Location where : adjacent) {
			Object animal = field.getAnimalAt(where);
			if (animal instanceof Animal) {
				Animal animalNear = (Animal) animal;
				if (animalNear.getClass() == this.getClass() && getGender() != animalNear.getGender()) {
					return true;
				}
			}
		}
		return false;
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


	protected void becomeSick() {
		if (!isSick()) {
			int randomNumber = rand.nextInt(getSickProbability());
			if (randomNumber == 1) {
				toggleSick();
			}
		}
	}


	protected void notSick() {
		if (isSick()) {
			int randomNumber = rand.nextInt(getRecoverProbability());
			if (randomNumber == 1) {
				toggleSick();
				sickStep = 0;
			}
		}
	}


	protected void battleSickness() {
		if (sick) {
			if (sickStep >= maxSickStep) {
				setDead();
				return;
			}
			sickStep++;
			Field field = getField();
			if (field != null) {
				List<Location> adjacent = field.adjacentAnimalLocations(getLocation());
				Iterator<Location> it = adjacent.iterator();
				while (it.hasNext()) {
					Location where = it.next();
					Object animal = field.getAnimalAt(where);
					if (animal instanceof Animal) {
						Animal nearAnimal = (Animal) animal;
						if (nearAnimal.getClass().equals(this.getClass())) {
							nearAnimal.becomeSick();
						}
					}
				}
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


	protected void toggleNocturnal() {
		nocturnal = !nocturnal;

	}


	protected void toggleSick() {
		sick = !sick;

	}


	protected int getSickProbability() {
		return sickProbability;
	}


	protected void setSickProbability(int inputValue) {
		sickProbability = inputValue;
	}


	protected int getRecoverProbability() {
		return recoverProbability;
	}


	protected void setRecoverProbability(int inputValue) {
		recoverProbability = inputValue;
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


	abstract protected int getBreedingAge();


	abstract protected int getMaxAge();


	abstract protected double getBreedingProbability();


	abstract protected int getMaxLitterSize();


	abstract protected Animal createNewAnimal(boolean randomAge, Field field, Location loc);
}
