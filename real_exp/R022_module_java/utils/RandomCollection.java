package utils;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * Data structure to choose items from a weighed distribution.
 * Adapted from: https://stackoverflow.com/questions/6409652/random-weighted-selection-in-java
 */
public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;
    private double total = 0;

    /**
     * Constructor for RandomCollection.
     */
    public RandomCollection() {
        this(new Random());
    }

    /**
     * Constructor for RandomCollection
     * @param random The Random Object
     */
    public RandomCollection(Random random) {
        this.random = random;
    }

    /**
     * Add an item to the collection
     * @param weight the weight of the object
     * @param result the object
     * @return this
     */
    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    /**
     * Get the next weighed random value from the collection.
     * @return the next value
     */
    public E next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }
}