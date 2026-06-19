import java.util.Random;


/**
 * The illness an {@link Animal} can carry. Encapsulates the full disease
 * lifecycle — catching it, carrying it for a number of steps, and recovering
 * (or succumbing) — so the host only has to orchestrate when each happens.
 *
 * <p>The "resistance" values are odds: a higher number means the matching
 * event is less likely, since it succeeds only on a 1-in-N dice roll.
 */
public class Disease {

	private static final Random rand = Randomizer.getRandom();

	private boolean active;

	private int infectionResistance;

	private int recoveryResistance;

	private int maxDuration;

	private int duration;


	public Disease(int infectionResistance) {
		this.infectionResistance = infectionResistance;
	}


	public boolean isActive() {
		return active;
	}


	/** Whether the illness has now been carried longer than it can be survived. */
	public boolean isTerminal() {
		return active && duration >= maxDuration;
	}


	/** Advance one step of an active illness. */
	public void advance() {
		duration++;
	}


	/** Attempt to contract the illness; a no-op if already infected. */
	public void tryInfect() {
		if (!active && rand.nextInt(infectionResistance) == 1) {
			active = true;
		}
	}


	/** Attempt to shake off the illness; a no-op if currently healthy. */
	public void tryRecover() {
		if (active && rand.nextInt(recoveryResistance) == 1) {
			active = false;
			duration = 0;
		}
	}


	public void setInfectionResistance(int infectionResistance) {
		this.infectionResistance = infectionResistance;
	}


	public void setRecoveryResistance(int recoveryResistance) {
		this.recoveryResistance = recoveryResistance;
	}


	public void setMaxDuration(int maxDuration) {
		this.maxDuration = maxDuration;
	}
}
