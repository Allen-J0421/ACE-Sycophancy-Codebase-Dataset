import java.awt.*;


public class Duck extends Prey {

	private static final AnimalSpecies SPECIES = AnimalSpecies.builder()
			.breedingAge(4)
			.maxAge(50)
			.breedingProbability(0.20)
			.maxLitterSize(5)
			.foodChainLevel(1)
			.foodValue(5)
			.sickProbability(10)
			.recoverProbability(2)
			.maxSickStep(20)
			.factory(Duck::new)
			.build();


	public Duck(boolean randomAge, FieldEnvironment field, Location location) {
		super(randomAge, field, location, SPECIES);
	}


	protected Color getObjectColor(Climate climate) {
		return new Color(241, 200, 23);
	}
}
