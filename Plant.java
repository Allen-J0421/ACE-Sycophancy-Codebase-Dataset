import java.util.Random;


public abstract class Plant extends Entity {


	private static final Random rand = Randomizer.getRandom();

	private static final double RAIN_GROWTH_PROBABILITY = 0.33;

	private int stage;

	private int maxStage;


	public Plant(Field field, Location location) {
		super(field, location);
	}


	@Override
	protected void placeInField(Field field, Location location) {
		field.placePlant(this, location);
	}


	protected void increaseStage(Climate climate) {
		if (stage < getMaxStage() && climate.getCurrentWeather() == Weather.RAIN
				&& rand.nextDouble() <= RAIN_GROWTH_PROBABILITY) {
			stage++;
		}
	}


	protected int getMaxStage() {
		return maxStage;
	}


	protected void setMaxStage(int maxStage) {
		this.maxStage = maxStage;
	}


	protected void reduceStage() {
		stage--;
	}


	protected boolean canEat() {
		return stage > 0;
	}


	protected int getStage() {
		return stage;
	}


	protected void setStage(int stage) {
		this.stage = stage;
	}
}
