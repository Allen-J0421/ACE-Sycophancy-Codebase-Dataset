import java.awt.*;


public class Bear extends Predator {

	private static final AnimalSpecies SPECIES = AnimalSpecies.builder()
			.breedingAge(40)
			.maxAge(300)
			.breedingProbability(0.18)
			.maxLitterSize(5)
			.foodChainLevel(3)
			.foodValue(30)
			.sickProbability(10)
			.recoverProbability(4)
			.maxSickStep(30)
			.additionalFoodValue(40)
			.factory(Bear::new)
			.build();


	public Bear(boolean randomAge, EntityController controller, Location location) {
		super(randomAge, controller, location, SPECIES);
	}


	protected Color getObjectColor(Climate climate) {
		return new Color(69, 54, 48);
	}
}
