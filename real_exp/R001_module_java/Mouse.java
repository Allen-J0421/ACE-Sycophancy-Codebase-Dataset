import java.awt.*;


public class Mouse extends Prey {


	private static final int BREEDING_AGE = 3;

	private static final int MAX_AGE = 40;

	private static final double BREEDING_PROBABILITY = 0.20;

	private static final int MAX_LITTER_SIZE = 8;


	public Mouse(boolean randomAge, Field field, Location location) {
		super(randomAge, field, location);
		setFoodChainLevel(1);
		setFoodValue(7);
		setSickProbability(50);
		setRecoverProbability(7);
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
		return new Mouse(randomAge, field, loc);
	}


	protected Color getObjectColor(Climate climate) {
		return new Color(132, 132, 130);
	}
}
