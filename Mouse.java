import java.awt.*;


public class Mouse extends Prey {


	private static final AnimalTraits TRAITS = new AnimalTraits(3, 40, 0.20, 8, 1, 7, 50, 7);


	public Mouse(boolean randomAge, Field field, Location location) {
		super(randomAge, field, location, TRAITS);
	}


	protected Animal createNewAnimal(boolean randomAge, Field field, Location loc) {
		return new Mouse(randomAge, field, loc);
	}


	protected Color getObjectColor(Climate climate) {
		return new Color(132, 132, 130);
	}
}
