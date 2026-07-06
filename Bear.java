import java.awt.*;


public class Bear extends Animal {


	private static final int BREEDING_AGE = 40;

	private static final int MAX_AGE = 300;

	private static final double BREEDING_PROBABILITY = 0.18;

	private static final int MAX_LITTER_SIZE = 5;


	public Bear(boolean randomAge, Field field, Location location) {
		super(field, location,
		      new PredatorHungerStrategy(40, false),
		      new StandardMovementStrategy(),
		      new StandardBreedingStrategy(),
		      true);
		setFoodChainLevel(3);
		setFoodValue(30);
		setSickProbability(10);
		setRecoverProbability(4);
		setMaxSickStep(30);
		if (randomAge) {
			setAge(rand.nextInt(MAX_AGE));
			setFoodLevel(rand.nextInt(5));
		} else {
			setAge(0);
			setFoodLevel(6);
		}
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
		return new Bear(randomAge, field, loc);
	}


	protected Color getObjectColor(Climate climate) {
		return new Color(69, 54, 48);
	}
}
