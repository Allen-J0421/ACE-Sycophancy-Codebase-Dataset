public abstract class Plant extends Entity {

	private static final RandomService RANDOM = RandomService.shared();

	private int stage;

	private int maxStage;


	public Plant(FieldEnvironment field, Location location) {
		super(field, location);
	}


	protected void increaseStage(Climate climate) {
		if (stage < getMaxStage()
				&& climate.getCurrentWeather() == Weather.RAIN
				&& RANDOM.chance(0.33)) {
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


	@Override
	protected void placeInField(FieldEnvironment field, Location location) {
		field.placePlant(this, location);
	}


	@Override
	protected void clearFieldLocation(FieldEnvironment field, Location location) {
		field.clearPlantAt(location);
	}
}
