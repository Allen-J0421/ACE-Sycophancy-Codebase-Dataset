import java.util.Iterator;
import java.util.List;
import java.util.Random;


public abstract class Animal extends Entity {


	protected static final Random rand = Randomizer.getRandom();

	private final HungerStrategy hungerStrategy;

	private final MovementStrategy movementStrategy;

	private final BreedingStrategy breedingStrategy;

	private final AgingStrategy agingStrategy;

	private final SicknessStrategy sicknessStrategy;

	private final boolean breedFirst;

	private Gender gender = Gender.MALE;

	private boolean nocturnal;

	private int foodChainLevel;

	private int foodValue;

	private int foodLevel;


	protected Animal(Field field, Location location,
	                 HungerStrategy hungerStrategy,
	                 MovementStrategy movementStrategy,
	                 BreedingStrategy breedingStrategy,
	                 AgingStrategy agingStrategy,
	                 SicknessStrategy sicknessStrategy,
	                 boolean breedFirst) {
		super(field, location);
		this.hungerStrategy = hungerStrategy;
		this.movementStrategy = movementStrategy;
		this.breedingStrategy = breedingStrategy;
		this.agingStrategy = agingStrategy;
		this.sicknessStrategy = sicknessStrategy;
		this.breedFirst = breedFirst;
		gender = gender.randomGender();
		nocturnal = false;
	}


	public FieldOccupant.Layer getOccupantLayer() {
		return FieldOccupant.Layer.ANIMAL;
	}


	public void act(List<Animal> newAnimals, TimeCycle time) {
		agingStrategy.tick(this);
		hungerStrategy.tick(this);
		sicknessStrategy.tick(this);
		if (isAlive()) {
			if (time == TimeCycle.NIGHT && isNocturnal()) {

				normalAct(newAnimals);
			} else if (time == TimeCycle.DAY && isNocturnal()) {

				normalAct(newAnimals);
			} else if (time == TimeCycle.DAY && !isNocturnal()) {

				normalAct(newAnimals);
			}
		}
	}


	private void normalAct(List<Animal> newAnimals) {
		if (breedFirst) {
			breedingStrategy.breed(this, newAnimals);
		}
		Location foodLocation = hungerStrategy.findFood(this);
		if (!breedFirst) {
			breedingStrategy.breed(this, newAnimals);
		}
		if (isAlive()) {
			movementStrategy.move(this, foodLocation);
		}
	}


	private boolean canBreed() {
		Field field = getField();
		List<Location> adjacent = field.adjacentAnimalLocations(getLocation());
		Iterator<Location> it = adjacent.iterator();
		boolean returnValue = agingStrategy.getAge() >= getBreedingAge();
		while (it.hasNext()) {
			Location where = it.next();
			Object animal = field.getAnimalAt(where);
			if (animal instanceof Animal) {
				Animal animalNear = (Animal) animal;

				if (animalNear.getClass().equals(this.getClass()) && getGender() != animalNear.getGender()) {
					return returnValue;
				}
			}
		}
		return returnValue;
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


	public boolean isSick() {
		return sicknessStrategy.isSick();
	}


	protected void becomeSick() {
		sicknessStrategy.becomeSick();
	}


	protected void toggleNocturnal() {
		nocturnal = !nocturnal;

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


	public int getFoodLevel() {
		return foodLevel;
	}


	public void setFoodLevel(int foodLevel) {
		this.foodLevel = foodLevel;
	}


	abstract protected int getBreedingAge();


	abstract protected double getBreedingProbability();


	abstract protected int getMaxLitterSize();


	abstract protected Animal createNewAnimal(boolean randomAge, Field field, Location loc);
}
