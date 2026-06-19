import java.awt.Color;


public class Duck extends Prey {

	private static final AnimalTraits TRAITS = AnimalTraits.builder()
			.breedingAge(4).maxAge(50).breedingProbability(0.20).maxLitterSize(5)
			.foodChainLevel(1).foodValue(5)
			.infectionResistance(10).recoveryResistance(2).maxSickStep(20)
			.color(new Color(241, 200, 23))
			.build();


	public Duck(boolean randomAge, Field field, Location location) {
		super(TRAITS, randomAge, field, location);
	}


	@Override
	protected Animal createNewAnimal(boolean randomAge, Field field, Location loc) {
		return new Duck(randomAge, field, loc);
	}
}
