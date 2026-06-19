import java.awt.*;


public class Bear extends Predator {


	private static final int BREEDING_AGE = 40;

	private static final int MAX_AGE = 300;

	private static final double BREEDING_PROBABILITY = 0.18;

	private static final int MAX_LITTER_SIZE = 5;


	public Bear(boolean randomAge, Field field, Location location) {
		super(randomAge, field, location);
		setFoodChainLevel(3);
		setFoodValue(30);
		setSickProbability(10);
		setRecoverProbability(4);
		setAdditionalFoodValue(40);
	}


	@Override
	protected int getBreedingAge() {
		return BREEDING_AGE;
	}


	@Override
	protected int getMaxAge() {
		return MAX_AGE;
	}


	@Override
	protected double getBreedingProbability() {
		return BREEDING_PROBABILITY;
	}


	@Override
	protected int getMaxLitterSize() {
		return MAX_LITTER_SIZE;
	}


	@Override
	protected Animal createNewAnimal(boolean randomAge, Field field, Location loc) {
		return new Bear(randomAge, field, loc);
	}


	@Override
	protected Color getObjectColor(Climate climate) {
		return new Color(69, 54, 48);
	}
}
