import java.awt.*;


public class Wolf extends Predator {


	private static final AnimalTraits TRAITS = new AnimalTraits(20, 250, 0.17, 6, 2, 10, 15, 4);


	public Wolf(boolean randomAge, Field field, Location location) {
		super(randomAge, field, location, TRAITS);
		toggleNocturnal();
		toggleCannibal();
		setAdditionalFoodValue(9);
	}


	protected Animal createNewAnimal(boolean randomAge, Field field, Location loc) {
		return new Wolf(randomAge, field, loc);
	}


	protected Color getObjectColor(Climate climate) {
		return new Color(50, 50, 47);
	}
}
