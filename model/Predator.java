package model;

import config.AnimalTraits;
import config.Randomizer;

import java.util.List;
import java.util.Random;


public abstract class Predator extends Animal {


	private static final Random rand = Randomizer.getRandom();


	public Predator(AnimalTraits traits, boolean randomAge, Field field, Location location) {
		super(traits, field, location);
		if (randomAge) {
			setAge(rand.nextInt(getMaxAge()));
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

			if (newLocation != null) {
				setLocation(newLocation);
			} else {

				setDead();
			}
		}
	}


	private Location findFood() {
		Field field = getField();
		for (Location where : field.adjacentAnimalLocations(getLocation())) {
			Animal prey = field.getAnimalAt(where);
			if (prey == null || !prey.isAlive() || !canEat(prey)) {
				continue;
			}
			prey.setDead();
			setFoodLevel(prey.getFoodValue() + getAdditionalFoodValue());
			return where;
		}
		return null;
	}


	/**
	 * Whether this predator may eat the given animal: anything lower in the food
	 * chain, or — when starving and cannibalistic — its own kind.
	 */
	private boolean canEat(Animal other) {
		if (other.getFoodChainLevel() < getFoodChainLevel()) {
			return true;
		}
		return other.getFoodChainLevel() == getFoodChainLevel()
				&& isCannibal()
				&& getFoodLevel() < 2
				&& other.getClass().equals(getClass());
	}


	public boolean isCannibal() {
		return getTraits().cannibal();
	}


	protected int getAdditionalFoodValue() {
		return getTraits().additionalFoodValue();
	}
}
