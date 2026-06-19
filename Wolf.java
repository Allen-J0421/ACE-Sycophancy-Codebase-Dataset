import java.awt.*;


public class Wolf extends Predator {


	private static final int BREEDING_AGE = 20;

	private static final int MAX_AGE = 250;

	private static final double BREEDING_PROBABILITY = 0.17;

	private static final int MAX_LITTER_SIZE = 6;


	public Wolf(boolean randomAge, Field field, Location location) {
		super(randomAge, field, location);
		setNocturnal(true);
		setFoodChainLevel(2);
		setFoodValue(10);
		setCannibal(true);
		setSickProbability(15);
		setRecoverProbability(4);
		setAdditionalFoodValue(9);
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
		return new Wolf(randomAge, field, loc);
	}


	@Override
	protected Color getObjectColor(Climate climate) {
		return new Color(50, 50, 47);
	}
}
