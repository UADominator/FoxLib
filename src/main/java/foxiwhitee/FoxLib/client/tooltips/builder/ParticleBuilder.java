package foxiwhitee.FoxLib.client.tooltips.builder;

import foxiwhitee.FoxLib.client.tooltips.theme.elements.particle.ParticleConfig;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.particle.ParticleKind;

/**
 * A class that stores basic information about effects around tooltip
 */
@SuppressWarnings("all")
public final class ParticleBuilder {
    /**
     * Enum for easy choice of particle type
     */
    public enum Kind {
        /**
         * Shape: rhombus.
         * Fades nonlinearly — its transparency drops sharply towards the end of life
         */
        EMBER,
        /**
         * Shape: circle.
         * Moves horizontally in waves (left-right)
         */
        SPORE,
        /**
         * Shape: rhombus.
         * Actively flickers
         */
        STAR,
        /**
         * Shape: bold line.
         * Draws a vector line that stretches in the opposite direction from the direction of its movement
         */
        SPARK
    }

    private final Kind kind;
    private int baseTarget;
    private float widthFactor;
    private float spawnSpeed;
    private float minLife;
    private float maxLife;
    private float baseSpeed;
    private float randSpeed;
    private float accelY;

    ParticleBuilder(Kind kind) {
        this.kind = kind;
    }

    /**
     * Sets all values for tooltip particles
     * @param baseTarget Base quantity limit
     * @param widthFactor Width multiplier. Dynamically increases from tooltip width
     * @param spawnSpeed Spawn Speed
     * @param minLife Minimum Lifetime
     * @param maxLife Extra Lifetime
     * @param baseSpeed Base Departure Speed
     * @param randSpeed Random Speed App
     * @param accelY Y-axis acceleration
     * @return This
     */
    public ParticleBuilder all(int baseTarget,  float widthFactor, float spawnSpeed, float minLife, float maxLife, float baseSpeed, float randSpeed, float accelY) {
        this.baseTarget = baseTarget;
        this.widthFactor = widthFactor;
        this.spawnSpeed = spawnSpeed;
        this.minLife = minLife;
        this.maxLife = maxLife;
        this.baseSpeed = baseSpeed;
        this.randSpeed = randSpeed;
        this.accelY = accelY;
        return this;
    }

    /**
     * Sets size and gravity data
     * @param baseTarget Base quantity limit
     * @param widthFactor Width multiplier. Dynamically increases from tooltip width
     * @param accelY Y-axis acceleration
     * @return This
     */
    public ParticleBuilder data(int baseTarget,  float widthFactor, float accelY) {
        this.baseTarget = baseTarget;
        this.widthFactor = widthFactor;
        this.accelY = accelY;
        return this;
    }

    /**
     * Sets speed data
     * @param spawnSpeed Spawn Speed
     * @param baseSpeed Base Departure Speed
     * @param randSpeed Random Speed App
     * @return This
     */
    public ParticleBuilder speed(float spawnSpeed, float baseSpeed, float randSpeed) {
        this.spawnSpeed = spawnSpeed;
        this.baseSpeed = baseSpeed;
        this.randSpeed = randSpeed;
        return this;
    }

    /**
     * Sets lifetime data
     * @param minLife Minimum Lifetime
     * @param maxLife Extra Lifetime
     * @return This
     */
    public ParticleBuilder life(float minLife, float maxLife) {
        this.minLife = minLife;
        this.maxLife = maxLife;
        return this;
    }

    ParticleConfig build() {
        return new ParticleConfig(ParticleKind.values()[kind.ordinal()], baseTarget, widthFactor, spawnSpeed, minLife, maxLife, baseSpeed, randSpeed, accelY);
    }
}
