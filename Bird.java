import java.awt.*;


public class Bird extends Animal {


	private static final int BREEDING_AGE = 3;

	private static final int MAX_AGE = 50;

	private static final double BREEDING_PROBABILITY = 0.17;

	private static final int MAX_LITTER_SIZE = 7;


	public Bird(boolean randomAge, Field field, Location location) {
		super(field, location,
		      new PreyHungerStrategy(),
		      new StandardMovementStrategy(),
		      new StandardBreedingStrategy(),
		      new StandardAgingStrategy(randomAge ? rand.nextInt(MAX_AGE) : 0, MAX_AGE),
		      new StandardSicknessStrategy(10, 6, 20),
		      false);
		setFoodChainLevel(1);
		setFoodValue(5);
		setFoodLevel(6);
	}


	protected int getBreedingAge() {
		return BREEDING_AGE;
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
