import java.awt.Color;


public enum AnimalSpecies {
	BIRD(3, 50, 0.17, 7, 1, 5, 10, 6, 20, false, false, 0, 0.07, new Color(236, 110, 11)),
	MOUSE(3, 40, 0.20, 8, 1, 7, 50, 7, 20, false, false, 0, 0.07, new Color(132, 132, 130)),
	DUCK(4, 50, 0.20, 5, 1, 5, 10, 2, 20, false, false, 0, 0.07, new Color(241, 200, 23)),
	WOLF(20, 250, 0.17, 6, 2, 10, 15, 4, 30, true, true, 9, 0.03, new Color(50, 50, 47)),
	BEAR(40, 300, 0.18, 5, 3, 30, 10, 4, 30, false, false, 40, 0.03, new Color(69, 54, 48));

	private final int breedingAge;
	private final int maxAge;
	private final double breedingProbability;
	private final int maxLitterSize;
	private final int foodChainLevel;
	private final int foodValue;
	private final int sickProbability;
	private final int recoverProbability;
	private final int maxSickStep;
	private final boolean nocturnal;
	private final boolean cannibal;
	private final int additionalFoodValue;
	private final double creationProbability;
	private final Color color;


	AnimalSpecies(int breedingAge, int maxAge, double breedingProbability, int maxLitterSize,
			int foodChainLevel, int foodValue, int sickProbability, int recoverProbability,
			int maxSickStep, boolean nocturnal, boolean cannibal, int additionalFoodValue,
			double creationProbability, Color color) {
		this.breedingAge = breedingAge;
		this.maxAge = maxAge;
		this.breedingProbability = breedingProbability;
		this.maxLitterSize = maxLitterSize;
		this.foodChainLevel = foodChainLevel;
		this.foodValue = foodValue;
		this.sickProbability = sickProbability;
		this.recoverProbability = recoverProbability;
		this.maxSickStep = maxSickStep;
		this.nocturnal = nocturnal;
		this.cannibal = cannibal;
		this.additionalFoodValue = additionalFoodValue;
		this.creationProbability = creationProbability;
		this.color = color;
	}


	public Animal create(boolean randomAge, Field field, Location location) {
		switch (this) {
			case BIRD:
				return new Bird(randomAge, field, location);
			case DUCK:
				return new Duck(randomAge, field, location);
			case MOUSE:
				return new Mouse(randomAge, field, location);
			case WOLF:
				return new Wolf(randomAge, field, location);
			case BEAR:
				return new Bear(randomAge, field, location);
			default:
				throw new IllegalStateException("Unhandled species: " + this);
		}
	}


	public int getBreedingAge() {
		return breedingAge;
	}


	public int getMaxAge() {
		return maxAge;
	}


	public double getBreedingProbability() {
		return breedingProbability;
	}


	public int getMaxLitterSize() {
		return maxLitterSize;
	}


	public int getFoodChainLevel() {
		return foodChainLevel;
	}


	public int getFoodValue() {
		return foodValue;
	}


	public int getSickProbability() {
		return sickProbability;
	}


	public int getRecoverProbability() {
		return recoverProbability;
	}


	public int getMaxSickStep() {
		return maxSickStep;
	}


	public boolean isNocturnal() {
		return nocturnal;
	}


	public boolean isCannibal() {
		return cannibal;
	}


	public int getAdditionalFoodValue() {
		return additionalFoodValue;
	}


	public double getCreationProbability() {
		return creationProbability;
	}


	public Color getColor() {
		return color;
	}
}
