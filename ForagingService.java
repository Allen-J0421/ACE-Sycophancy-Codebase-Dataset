import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Encapsulates all foraging, eating, and carcass-generation logic that was
 * previously spread across Consumer.findFood() and Consumer.eat().
 * One instance is created per Consumer and held for its lifetime.
 */
public class ForagingService
{
    private static final double NIGHT_PREY_MISS_PROBABILITY = 0.5;
    private static final Random rand = Randomizer.getRandom();

    private final ArrayList<Class> prey;
    private final boolean canEatCarcass;

    public ForagingService(ArrayList<Class> prey, boolean canEatCarcass)
    {
        this.prey         = prey;
        this.canEatCarcass = canEatCarcass;
    }

    /**
     * Scan cells adjacent to {@code consumer} for prey or a carcass.
     * Returns a {@link ForagingResult} describing what was found (and consumed);
     * {@link ForagingResult#NO_FOOD} when nothing edible was reachable.
     * Does NOT modify any field on {@code consumer} — the caller applies the result.
     */
    public ForagingResult forage(Consumer consumer)
    {
        Field field = consumer.getField();

        for (Location where : field.adjacentLocations(consumer.getLocation()))
        {
            Object object = field.getObjectAt(where);

            // Check for live prey (mirrors the for-each over prey inside the while loop):
            for (Class preyClass : prey)
            {
                if (preyClass.isInstance(object))
                {
                    Actor actor = (Actor) object;
                    if (actor.getIsAlive())
                    {
                        boolean canCapture;
                        if (TimeSystem.isNightTime())
                        {
                            canCapture = rand.nextDouble() <= 1.0 - NIGHT_PREY_MISS_PROBABILITY;
                        }
                        else
                        {
                            canCapture = true;
                        }
                        if (canCapture)
                        {
                            ForagingResult result = computeEat(consumer, actor);
                            if (result != null) return result;
                        }
                    }
                }
            }

            // Check for a carcass (checked after the prey loop, matching original order):
            if (Carcass.class.isInstance(object) && canEatCarcass)
            {
                Carcass carcass = (Carcass) object;
                boolean diseased = carcass.isDiseased();
                int worth = carcass.getConsumptionWorth();
                carcass.setDead();
                return new ForagingResult(where, worth, false, null, diseased);
            }
        }

        return ForagingResult.NO_FOOD;
    }

    /**
     * Attempt to eat a live prey actor and compute the resulting sustenance change
     * and optional overflow carcass.  Returns null when eating is not possible
     * (e.g. the prey does not become a carcass and the consumer is overfull).
     */
    private ForagingResult computeEat(Consumer consumer, Actor actor)
    {
        Location preyLocation = actor.getLocation();
        int worth = actor.getConsumptionWorth();
        int sustenance = consumer.getSustenanceLevel();
        int maxSustenance = consumer.getMaxSustenanceForEating();

        if (actor.becomeCarcass())
        {
            actor.setDead();
            if (sustenance + worth > maxSustenance)
            {
                int foodLeft = (sustenance + consumer.getConsumptionWorth()) - maxSustenance;
                Carcass carcass = new Carcass(consumer.getField(), preyLocation, foodLeft);
                return new ForagingResult(consumer.getLocation(), 0, true, carcass, false);
            }
        }
        if (sustenance + worth <= maxSustenance)
        {
            return new ForagingResult(preyLocation, worth, false, null, false);
        }
        return null;
    }
}
