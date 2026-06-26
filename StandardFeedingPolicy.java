import java.util.Random;

/**
 * Default feeding behavior shared by animals and eagles.
 */
public final class StandardFeedingPolicy implements FeedingPolicy
{
    private static final Random rand = Randomizer.getRandom();

    @Override
    public void feed(Animal consumer, Organism prey)
    {
        if (prey.isDiseased()
                && prey.getDisease().getDiseaseType() == DiseaseType.FOODBORNE
                && prey.getDisease().getPropagationRate() <= rand.nextDouble())
        {
            consumer.setDisease(prey.getDisease());
        }

        if(prey.isAlive())
        {
            prey.setDead();
            int newFoodLevel = consumer.foodLevel + ((Edible) prey).getFoodValue();
            consumer.foodLevel = Math.min(newFoodLevel, consumer.MAX_FOOD_LEVEL());
        }
    }
}
