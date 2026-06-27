import java.util.List;
import java.util.Random;

/**
 * Defines what the different characteristics of actors in the simulation must have
 *
 * Determines it's state of life and location on the field
 * Creates existence of action method for subclasses 
 *
 * @version (28/02/2022)
 */
public abstract class Actor
{
    // Whether the actor is alive or not.
    protected boolean alive;
    // The actor's field.
    protected Field field;
    // The actor's position in the field.
    protected Location location;
    // Whether the actor can overlap with other actors in a particular field
    protected boolean overlap;
    // The current phase (time of day) of the simulation step.
    private Phase phase;
    // The strategy mapping each phase to the behaviour run during it.
    private Schedule schedule;
    // The current weather conditions in the field
    protected Weather weather;
    // A shared random number generator to control breeding and disease infection effects.
    protected static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Actor
     * 
     * @param field The field the actor is currently in
     * @param location The actors location on the field
     */
    protected Actor(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);

        // Default strategy: run each phase's own action. Subclasses provide
        // dayAct/nightAct; callers can swap in a different Schedule to change
        // the day/night behaviour without modifying Actor.act.
        schedule = new Schedule()
            .on(Phase.DAY, this::dayAct)
            .on(Phase.NIGHT, this::nightAct);
    }

    /**
     * Replaces actor onto field if alive and space available.
     * Sets current weather and determines effects.
     * Runs the action for the given phase (time of day).
     *
     * @param newActors A list to return newly born actors.
     * @param phase     The phase (day or night) of this step.
     * @param weather   The current weather conditions.
     */
    public void act(List<Actor> newActors, Phase phase, Weather weather)
    {
        overcrowding();
        replaceActor();

        // collects weather effect values for current weather
        this.weather = weather;
        setWeatherEffects();

        // Records the phase and delegates to the schedule strategy, which
        // decides what behaviour runs for this phase. Swapping the schedule
        // changes the day/night behaviour without changing this method.
        this.phase = phase;
        schedule.run(phase, newActors);
    }

    /**
     * Replace this actor's phase behaviour strategy. Lets the simulation swap
     * day/night schedules (or install entirely new ones) without modifying how
     * act works.
     *
     * @param schedule The new schedule to use.
     */
    public void setSchedule(Schedule schedule)
    {
        this.schedule = schedule;
    }

    /**
     * Return this actor's current phase behaviour strategy. Useful for deriving
     * a variant, e.g. {@code actor.setSchedule(actor.getSchedule().swapped(Phase.DAY, Phase.NIGHT))}.
     *
     * @return The current schedule.
     */
    public Schedule getSchedule()
    {
        return schedule;
    }

    /**
     * Used to place alive actors back onto the field at their location if their space is available
     */
    private void replaceActor()
    {

        if ((alive) && (field.getObjectAt(location) == null)) {
            field.place(this, location);
        }
    }

    /**
     * Kills actors that are not on the field that must be on field to survive
     */
    private void overcrowding()
    {
        if(!overlap && location!=null && field.getObjectAt(location)!=null && !this.equals(field.getObjectAt(location))) {
            alive = false;
            location = null;
            field = null;
        }
    }

    /**
     * Returns whether the current phase is day.
     *
     * @return true if the current phase is day, false if night.
     */
    protected boolean getDay()
    {
        return phase == Phase.DAY;
    }

    /**
     * Returns if the actor is infected or not
     * 
     * @return actor always not infected
     */
    public boolean getInfected()
    {
        return false;
    }

    /**
     * Plays out the actions taken by the actor during the day.
     * 
     * @param newActors A list to return newly born actors.
     */
    protected abstract void dayAct(List<Actor> newActors);

    /**
     * Plays out the actions taken by the actor during the night.
     * 
     * @param newActors A list to return newly born actors.
     */
    protected abstract void nightAct(List<Actor> newActors);

    /**
     * Gets food value of animal or plants
     * 
     */
    protected abstract double getFoodValue();

    /**
     * Sets current weather effects values
     * 
     */
    protected abstract void setWeatherEffects();

    protected void setOverlap(boolean overlapping) {
        this.overlap = overlapping;
    }

    /**
     * Check whether the actor is alive or not.
     * 
     * @return true if the actor is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the actor is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the actor's location.
     * 
     * @return The actor's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Places the actor at the new location in the given field.
     * 
     * @param newLocation The actor's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Return the actor's field.
     * 
     * @return The actor's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Return Whether the actor can overlap with other actors.
     * 
     * @return Whether the actor can overlap.
     */
    public boolean getOverlap()
    {
        return overlap;
    }
}
