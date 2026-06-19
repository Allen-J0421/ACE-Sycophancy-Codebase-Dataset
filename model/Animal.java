package model;

import config.AnimalTraits;
import config.Randomizer;

import java.awt.Color;
import java.util.List;
import java.util.Random;


public abstract class Animal extends Entity {


	private static final Random rand = Randomizer.getRandom();

	/** Immutable per-species configuration; the single source of these constants. */
	private final AnimalTraits traits;

	private final Disease disease;

	private boolean alive;

	private int age;

	private Gender gender;

	private int foodLevel;


	public Animal(AnimalTraits traits, Field field, Location location) {
		super(field, location);
		this.traits = traits;
		alive = true;
		gender = Gender.random();
		disease = new Disease(traits.infectionResistance());
		disease.setRecoveryResistance(traits.recoveryResistance());
		disease.setMaxDuration(traits.maxSickStep());
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


	@Override
	public Color getObjectColor(Climate climate) {
		return traits.color();
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


	public boolean isAlive() {
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


	protected AnimalTraits getTraits() {
		return traits;
	}


	protected Gender getGender() {
		return gender;
	}


	protected int getFoodChainLevel() {
		return traits.foodChainLevel();
	}


	protected int getFoodValue() {
		return traits.foodValue();
	}


	protected boolean isNocturnal() {
		return traits.nocturnal();
	}


	protected int getBreedingAge() {
		return traits.breedingAge();
	}


	protected int getMaxAge() {
		return traits.maxAge();
	}


	protected double getBreedingProbability() {
		return traits.breedingProbability();
	}


	protected int getMaxLitterSize() {
		return traits.maxLitterSize();
	}


	public boolean isSick() {
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


	abstract protected Animal createNewAnimal(boolean randomAge, Field field, Location loc);
}
