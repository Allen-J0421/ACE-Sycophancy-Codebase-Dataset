package simulation;

/**
 * Event emitted when a consumer eats or tramples something.
 */
public final class FoodConsumptionEvent extends SimulationEvent
{
    private final LivingEntity consumer;
    private final Object food;
    private final int foodValue;

    public FoodConsumptionEvent(Object source, LivingEntity consumer, Object food, int foodValue) {
        super(SimulationEventType.FOOD_CONSUMED, source);
        this.consumer = consumer;
        this.food = food;
        this.foodValue = foodValue;
    }

    public LivingEntity getConsumer() {
        return consumer;
    }

    public Object getFood() {
        return food;
    }

    public int getFoodValue() {
        return foodValue;
    }
}
