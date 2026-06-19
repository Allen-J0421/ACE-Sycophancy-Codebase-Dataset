package config;

import java.awt.Color;


/**
 * Immutable, declarative configuration for a species of {@link Animal}: how it
 * breeds, where it sits in the food chain, how it handles illness, and how it
 * looks. Each concrete animal class owns a single shared {@code AnimalTraits}
 * constant, so its behaviour is described in one place rather than scattered
 * across constructor side effects.
 *
 * <p>Built via {@link #builder()} for readable, order-independent definitions.
 */
public final class AnimalTraits {

	private final int breedingAge;
	private final int maxAge;
	private final double breedingProbability;
	private final int maxLitterSize;
	private final int foodChainLevel;
	private final int foodValue;
	private final int infectionResistance;
	private final int recoveryResistance;
	private final int maxSickStep;
	private final boolean nocturnal;
	private final boolean cannibal;
	private final int additionalFoodValue;
	private final Color color;


	private AnimalTraits(Builder builder) {
		this.breedingAge = builder.breedingAge;
		this.maxAge = builder.maxAge;
		this.breedingProbability = builder.breedingProbability;
		this.maxLitterSize = builder.maxLitterSize;
		this.foodChainLevel = builder.foodChainLevel;
		this.foodValue = builder.foodValue;
		this.infectionResistance = builder.infectionResistance;
		this.recoveryResistance = builder.recoveryResistance;
		this.maxSickStep = builder.maxSickStep;
		this.nocturnal = builder.nocturnal;
		this.cannibal = builder.cannibal;
		this.additionalFoodValue = builder.additionalFoodValue;
		this.color = builder.color;
	}


	public static Builder builder() {
		return new Builder();
	}


	public int breedingAge() {
		return breedingAge;
	}

	public int maxAge() {
		return maxAge;
	}

	public double breedingProbability() {
		return breedingProbability;
	}

	public int maxLitterSize() {
		return maxLitterSize;
	}

	public int foodChainLevel() {
		return foodChainLevel;
	}

	public int foodValue() {
		return foodValue;
	}

	public int infectionResistance() {
		return infectionResistance;
	}

	public int recoveryResistance() {
		return recoveryResistance;
	}

	public int maxSickStep() {
		return maxSickStep;
	}

	public boolean nocturnal() {
		return nocturnal;
	}

	public boolean cannibal() {
		return cannibal;
	}

	public int additionalFoodValue() {
		return additionalFoodValue;
	}

	public Color color() {
		return color;
	}


	/** Fluent builder; behavioural flags default to "off" so most species omit them. */
	public static final class Builder {

		private int breedingAge;
		private int maxAge;
		private double breedingProbability;
		private int maxLitterSize;
		private int foodChainLevel;
		private int foodValue;
		private int infectionResistance;
		private int recoveryResistance;
		private int maxSickStep;
		private boolean nocturnal = false;
		private boolean cannibal = false;
		private int additionalFoodValue = 0;
		private Color color;

		private Builder() {
		}

		public Builder breedingAge(int value) {
			this.breedingAge = value;
			return this;
		}

		public Builder maxAge(int value) {
			this.maxAge = value;
			return this;
		}

		public Builder breedingProbability(double value) {
			this.breedingProbability = value;
			return this;
		}

		public Builder maxLitterSize(int value) {
			this.maxLitterSize = value;
			return this;
		}

		public Builder foodChainLevel(int value) {
			this.foodChainLevel = value;
			return this;
		}

		public Builder foodValue(int value) {
			this.foodValue = value;
			return this;
		}

		public Builder infectionResistance(int value) {
			this.infectionResistance = value;
			return this;
		}

		public Builder recoveryResistance(int value) {
			this.recoveryResistance = value;
			return this;
		}

		public Builder maxSickStep(int value) {
			this.maxSickStep = value;
			return this;
		}

		public Builder nocturnal(boolean value) {
			this.nocturnal = value;
			return this;
		}

		public Builder cannibal(boolean value) {
			this.cannibal = value;
			return this;
		}

		public Builder additionalFoodValue(int value) {
			this.additionalFoodValue = value;
			return this;
		}

		public Builder color(Color value) {
			this.color = value;
			return this;
		}

		public AnimalTraits build() {
			return new AnimalTraits(this);
		}
	}
}
