import java.awt.*;


public class Wolf extends Predator {

	private static final AnimalSpecies SPECIES = AnimalSpecies.builder()
			.breedingAge(20)
			.maxAge(250)
			.breedingProbability(0.17)
			.maxLitterSize(6)
			.foodChainLevel(2)
			.foodValue(10)
			.sickProbability(15)
			.recoverProbability(4)
			.maxSickStep(30)
			.nocturnal(true)
			.cannibal(true)
			.additionalFoodValue(9)
			.factory(Wolf::new)
			.build();


	public Wolf(boolean randomAge, FieldEnvironment field, Location location) {
		super(randomAge, field, location, SPECIES);
	}


	protected Color getObjectColor(Climate climate) {
		return new Color(50, 50, 47);
	}
}
