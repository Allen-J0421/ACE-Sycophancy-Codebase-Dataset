import java.util.Objects;


public class AnimalSpecies {

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
	private final AnimalFactory factory;


	private AnimalSpecies(Builder builder) {
		breedingAge = builder.breedingAge;
		maxAge = builder.maxAge;
		breedingProbability = builder.breedingProbability;
		maxLitterSize = builder.maxLitterSize;
		foodChainLevel = builder.foodChainLevel;
		foodValue = builder.foodValue;
		sickProbability = builder.sickProbability;
		recoverProbability = builder.recoverProbability;
		maxSickStep = builder.maxSickStep;
		nocturnal = builder.nocturnal;
		cannibal = builder.cannibal;
		additionalFoodValue = builder.additionalFoodValue;
		factory = Objects.requireNonNull(builder.factory, "factory");
	}


	public static Builder builder() {
		return new Builder();
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


	public Animal createAnimal(boolean randomAge, FieldEnvironment environment, Location location) {
		return factory.create(randomAge, environment, location);
	}


	public static class Builder {
		private int breedingAge;
		private int maxAge;
		private double breedingProbability;
		private int maxLitterSize;
		private int foodChainLevel;
		private int foodValue;
		private int sickProbability;
		private int recoverProbability;
		private int maxSickStep;
		private boolean nocturnal;
		private boolean cannibal;
		private int additionalFoodValue;
		private AnimalFactory factory;


		public Builder breedingAge(int breedingAge) {
			this.breedingAge = breedingAge;
			return this;
		}


		public Builder maxAge(int maxAge) {
			this.maxAge = maxAge;
			return this;
		}


		public Builder breedingProbability(double breedingProbability) {
			this.breedingProbability = breedingProbability;
			return this;
		}


		public Builder maxLitterSize(int maxLitterSize) {
			this.maxLitterSize = maxLitterSize;
			return this;
		}


		public Builder foodChainLevel(int foodChainLevel) {
			this.foodChainLevel = foodChainLevel;
			return this;
		}


		public Builder foodValue(int foodValue) {
			this.foodValue = foodValue;
			return this;
		}


		public Builder sickProbability(int sickProbability) {
			this.sickProbability = sickProbability;
			return this;
		}


		public Builder recoverProbability(int recoverProbability) {
			this.recoverProbability = recoverProbability;
			return this;
		}


		public Builder maxSickStep(int maxSickStep) {
			this.maxSickStep = maxSickStep;
			return this;
		}


		public Builder nocturnal(boolean nocturnal) {
			this.nocturnal = nocturnal;
			return this;
		}


		public Builder cannibal(boolean cannibal) {
			this.cannibal = cannibal;
			return this;
		}


		public Builder additionalFoodValue(int additionalFoodValue) {
			this.additionalFoodValue = additionalFoodValue;
			return this;
		}


		public Builder factory(AnimalFactory factory) {
			this.factory = factory;
			return this;
		}


		public AnimalSpecies build() {
			return new AnimalSpecies(this);
		}
	}
}
