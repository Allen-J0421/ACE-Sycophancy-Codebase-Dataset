import java.awt.*;


public class Duck extends Prey {


	private static final int BREEDING_AGE = 4;

	private static final int MAX_AGE = 50;

	private static final double BREEDING_PROBABILITY = 0.20;

	private static final int MAX_LITTER_SIZE = 5;


	public Duck(boolean randomAge, Field field, Location location) {
		super(randomAge, field, location);
		setFoodChainLevel(1);
		setFoodValue(5);
		setSickProbability(10);
		setRecoverProbability(2);
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
		return new Duck(randomAge, field, loc);
	}


	@Override
	protected Color getObjectColor(Climate climate) {
		return new Color(241, 200, 23);
	}
}
