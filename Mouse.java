import java.awt.*;


public class Mouse extends Prey {

	private static final AnimalSpecies SPECIES = AnimalSpecies.builder()
			.breedingAge(3)
			.maxAge(40)
			.breedingProbability(0.20)
			.maxLitterSize(8)
			.foodChainLevel(1)
			.foodValue(7)
			.sickProbability(50)
			.recoverProbability(7)
			.maxSickStep(20)
			.factory(Mouse::new)
			.build();


	public Mouse(boolean randomAge, EntityController controller, Location location) {
		super(randomAge, controller, location, SPECIES);
	}


	protected Color getObjectColor(Climate climate) {
		return new Color(132, 132, 130);
	}
}
