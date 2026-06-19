import java.awt.*;


public class Bird extends Prey {


	private static final AnimalTraits TRAITS = new AnimalTraits(3, 50, 0.17, 7, 1, 5, 10, 6);


	public Bird(boolean randomAge, Field field, Location location) {
		super(randomAge, field, location, TRAITS);
	}


	protected Animal createNewAnimal(boolean randomAge, Field field, Location loc) {
		return new Bird(randomAge, field, loc);
	}


	protected Color getObjectColor(Climate climate) {
		return new Color(236, 110, 11);
	}
}
