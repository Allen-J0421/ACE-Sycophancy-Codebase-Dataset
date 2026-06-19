import java.awt.*;


public class Duck extends Prey {


	private static final AnimalTraits TRAITS = new AnimalTraits(4, 50, 0.20, 5, 1, 5, 10, 2);


	public Duck(boolean randomAge, Field field, Location location) {
		super(randomAge, field, location, TRAITS);
	}


	protected Animal createNewAnimal(boolean randomAge, Field field, Location loc) {
		return new Duck(randomAge, field, loc);
	}


	protected Color getObjectColor(Climate climate) {
		return new Color(241, 200, 23);
	}
}
