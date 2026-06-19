import java.util.Random;


public abstract class Plant extends Entity {


	private static final Random rand = Randomizer.getRandom();

	private int stage;

	private int maxStage;


	public Plant(Field field, Location location) {
		super(field, location);
	}

	@Override
	public final OccupancyLayer getOccupancyLayer() {
		return OccupancyLayer.PLANT;
	}


	protected void increaseStage(Climate climate) {
		if (stage < getMaxStage()) {
			if (climate.getCurrentWeather() == Weather.RAIN) {
				if (rand.nextDouble() <= 0.33) {
					stage++;
				}
			}
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
