package model;

import config.AnimalTraits;

import java.awt.Color;


public class Wolf extends Predator {

	private static final AnimalTraits TRAITS = AnimalTraits.builder()
			.breedingAge(20).maxAge(250).breedingProbability(0.17).maxLitterSize(6)
			.foodChainLevel(2).foodValue(10)
			.infectionResistance(15).recoveryResistance(4).maxSickStep(30)
			.nocturnal(true).cannibal(true).additionalFoodValue(9)
			.color(new Color(50, 50, 47))
			.build();


	public Wolf(boolean randomAge, Field field, Location location) {
		super(TRAITS, randomAge, field, location);
	}


	@Override
	protected Animal createNewAnimal(boolean randomAge, Field field, Location loc) {
		return new Wolf(randomAge, field, loc);
	}
}
