import java.awt.*;


public class Bear extends Predator {


	private static final AnimalTraits TRAITS = new AnimalTraits(40, 300, 0.18, 5, 3, 30, 10, 4);


	public Bear(boolean randomAge, Field field, Location location) {
		super(randomAge, field, location, TRAITS);
		setAdditionalFoodValue(40);
	}


	protected Animal createNewAnimal(boolean randomAge, Field field, Location loc) {
		return new Bear(randomAge, field, loc);
	}


	protected Color getObjectColor(Climate climate) {
		return new Color(69, 54, 48);
	}
}
