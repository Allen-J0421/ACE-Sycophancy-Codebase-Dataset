import java.awt.Color;


public class Bird extends Prey {

	private static final AnimalTraits TRAITS = AnimalTraits.builder()
			.breedingAge(3).maxAge(50).breedingProbability(0.17).maxLitterSize(7)
			.foodChainLevel(1).foodValue(5)
			.infectionResistance(10).recoveryResistance(6).maxSickStep(20)
			.color(new Color(236, 110, 11))
			.build();


	public Bird(boolean randomAge, Field field, Location location) {
		super(TRAITS, randomAge, field, location);
	}


	@Override
	protected Animal createNewAnimal(boolean randomAge, Field field, Location loc) {
		return new Bird(randomAge, field, loc);
	}
}
