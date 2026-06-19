import java.awt.Color;


public class Mouse extends Prey {

	private static final AnimalTraits TRAITS = AnimalTraits.builder()
			.breedingAge(3).maxAge(40).breedingProbability(0.20).maxLitterSize(8)
			.foodChainLevel(1).foodValue(7)
			.infectionResistance(50).recoveryResistance(7).maxSickStep(20)
			.color(new Color(132, 132, 130))
			.build();


	public Mouse(boolean randomAge, Field field, Location location) {
		super(TRAITS, randomAge, field, location);
	}


	@Override
	protected Animal createNewAnimal(boolean randomAge, Field field, Location loc) {
		return new Mouse(randomAge, field, loc);
	}
}
