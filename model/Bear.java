package model;

import config.AnimalTraits;

import java.awt.Color;


public class Bear extends Predator {

	private static final AnimalTraits TRAITS = AnimalTraits.builder()
			.breedingAge(40).maxAge(300).breedingProbability(0.18).maxLitterSize(5)
			.foodChainLevel(3).foodValue(30)
			.infectionResistance(10).recoveryResistance(4).maxSickStep(30)
			.additionalFoodValue(40)
			.color(new Color(69, 54, 48))
			.build();


	public Bear(boolean randomAge, Field field, Location location) {
		super(TRAITS, randomAge, field, location);
	}


	@Override
	protected Animal createNewAnimal(boolean randomAge, Field field, Location loc) {
		return new Bear(randomAge, field, loc);
	}
}
