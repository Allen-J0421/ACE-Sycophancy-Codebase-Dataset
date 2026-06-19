import java.awt.*;


public class Bird extends Prey {


	private static final int BREEDING_AGE = 3;

	private static final int MAX_AGE = 50;

	private static final double BREEDING_PROBABILITY = 0.17;

	private static final int MAX_LITTER_SIZE = 7;


	public Bird(boolean randomAge, Field field, Location location) {
		super(randomAge, field, location);
		setFoodChainLevel(1);
		setFoodValue(5);
		setSickProbability(10);
		setRecoverProbability(6);
	}


	protected int getBreedingAge() {
		return BREEDING_AGE;
	}


	protected int getMaxAge() {
		return MAX_AGE;
	}


	protected double getBreedingProbability() {
		return BREEDING_PROBABILITY;
	}


	protected int getMaxLitterSize() {
		return MAX_LITTER_SIZE;
	}


	protected Animal createNewAnimal(boolean randomAge, Field field, Location loc) {
		return new Bird(randomAge, field, loc);
	}


	protected Color getObjectColor(Climate climate) {
		return new Color(236, 110, 11);
	}
}
