import java.awt.*;


public class Wolf extends Animal {


	private static final int BREEDING_AGE = 20;

	private static final int MAX_AGE = 250;

	private static final double BREEDING_PROBABILITY = 0.17;

	private static final int MAX_LITTER_SIZE = 6;


	public Wolf(boolean randomAge, Field field, Location location) {
		super(field, location,
		      new PredatorHungerStrategy(9, true),
		      new StandardMovementStrategy(),
		      new StandardBreedingStrategy(),
		      true);
		toggleNocturnal();
		setFoodChainLevel(2);
		setFoodValue(10);
		setSickProbability(15);
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
		return new Wolf(randomAge, field, loc);
	}


	protected Color getObjectColor(Climate climate) {
		return new Color(50, 50, 47);
	}
}
