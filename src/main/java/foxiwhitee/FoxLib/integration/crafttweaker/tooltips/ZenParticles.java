package foxiwhitee.FoxLib.integration.crafttweaker.tooltips;

import foxiwhitee.FoxLib.client.tooltips.builder.ParticleBuilder;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * ZenScript data holding bridge for environmental tooltips particle effects.
 */
@ZenClass("mods.foxlib.ZenParticles")
@SuppressWarnings("unused")
public final class ZenParticles {
    private final ParticleBuilder internal;

    /**
     * Sets up a scripting data interface node tracking particle metrics.
     * @param internal Native processor engine module instance
     */
    public ZenParticles(ParticleBuilder internal) {
        this.internal = internal;
    }

    /**
     * Extracts underlying engine module instance variables tracking particles.
     * @return Core native state ParticleBuilder block
     */
    public ParticleBuilder getInternal() {
        return this.internal;
    }

    /**
     * Configures all physical emission properties of the particle canvas block simultaneously.
     * @param count      Maximum concurrent running simulation limits
     * @param gravity    Physics downwards vector acceleration factor
     * @param speed      Initial scalar propulsion force magnitude
     * @param minLife    Minimum lifetime boundaries before ticks discard instances
     * @param maxLife    Maximum lifetime ticks allowed before forced deletion
     * @param width      Spawn positioning constraint boundaries box width width axis
     * @param height     Spawn positioning constraint boundaries box height height axis
     * @param hexColor   Target base color overlay filter value (HEX string or int)
     * @return This wrapper instance for method chaining
     */
    @ZenMethod
    public ZenParticles all(int count, float gravity, float speed, float minLife, float maxLife, float width, float height, String hexColor) {
        this.internal.all(count, gravity, speed, minLife, maxLife, width, height, ZenTooltipManager.parseColor(hexColor));
        return this;
    }

    /**
     * Sets structural distribution count constraints along with environmental physics settings.
     * @param count    Maximum active atmospheric instance counts
     * @param gravity  Downward falling environmental weight force scalar multiplier
     * @param hexColor Spatial rendering color mask tint applied directly to assets (HEX string or int)
     * @return This wrapper instance for method chaining
     */
    @ZenMethod
    public ZenParticles data(int count, float gravity, String hexColor) {
        this.internal.data(count, gravity, ZenTooltipManager.parseColor(hexColor));
        return this;
    }

    /**
     * Sets velocity vectors and initialization speed calculations.
     * @param speed  Absolute acceleration value magnitude forcing physics displacement
     * @param width  Horizontal randomized spawning variance bounding coordinates offset width
     * @param height Vertical randomized spawning variance bounding coordinates offset height
     * @return This wrapper instance for method chaining
     */
    @ZenMethod
    public ZenParticles speed(float speed, float width, float height) {
        this.internal.speed(speed, width, height);
        return this;
    }

    /**
     * Defines life span cycle limits calculating operational longevity bounds.
     * @param minLife Minimum ticks an engine slice keeps instances processing
     * @param maxLife Maximum ceiling lifetime bounds allowed before thread collection cleanup
     * @return This wrapper instance for method chaining
     */
    @ZenMethod
    public ZenParticles life(float minLife, float maxLife) {
        this.internal.life(minLife, maxLife);
        return this;
    }
}
