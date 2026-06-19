import java.awt.*;


public class Bird extends Prey {

	private static final AnimalSpecies SPECIES = AnimalSpecies.builder()
			.breedingAge(3)
			.maxAge(50)
			.breedingProbability(0.17)
			.maxLitterSize(7)
			.foodChainLevel(1)
			.foodValue(5)
			.sickProbability(10)
			.recoverProbability(6)
			.maxSickStep(20)
			.factory(Bird::new)
			.build();


	public Bird(boolean randomAge, EntityController controller, Location location) {
		super(randomAge, controller, location, SPECIES);
	}


	protected Color getObjectColor(Climate climate) {
		return new Color(236, 110, 11);
	}
}
