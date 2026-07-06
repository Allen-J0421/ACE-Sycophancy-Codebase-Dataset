import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class StandardSicknessStrategy implements SicknessStrategy {


	private static final Random rand = Randomizer.getRandom();

	private final int sickProbability;

	private final int recoverProbability;

	private final int maxSickStep;

	private boolean sick;

	private int sickStep;


	public StandardSicknessStrategy(int sickProbability, int recoverProbability, int maxSickStep) {
		this.sickProbability = sickProbability;
		this.recoverProbability = recoverProbability;
		this.maxSickStep = maxSickStep;
		this.sick = false;
		this.sickStep = 0;
	}


	public boolean isSick() {
		return sick;
	}


	public void becomeSick() {
		if (!sick) {
			if (rand.nextInt(sickProbability) == 1) {
				sick = true;
			}
		}
	}


	private void recover() {
		if (sick) {
			if (rand.nextInt(recoverProbability) == 1) {
				sick = false;
				sickStep = 0;
			}
		}
	}


	public void tick(Animal animal) {
		if (sick) {
			if (sickStep >= maxSickStep) {
				animal.setDead();
				return;
			}
			sickStep++;
			Field field = animal.getField();
			if (field != null) {
				List<Location> adjacent = field.adjacentAnimalLocations(animal.getLocation());
				Iterator<Location> it = adjacent.iterator();
				while (it.hasNext()) {
					Location where = it.next();
					Object obj = field.getAnimalAt(where);
					if (obj instanceof Animal) {
						Animal nearAnimal = (Animal) obj;
						if (nearAnimal.getClass().equals(animal.getClass())) {
							nearAnimal.becomeSick();
						}
					}
				}
				recover();
			}
		} else {
			becomeSick();
		}
	}

}
