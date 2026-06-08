package foxiwhitee.FoxLib.client.tooltips.theme;

public class ParticleConfig {
    public final ParticleKind kind;
    public final int baseTarget;
    public final float widthFactor;
    public final float spawnSpeed;
    public final float minLife;
    public final float maxLife;
    public final float baseSpeed;
    public final float randSpeed;
    public final float accelY;

    public ParticleConfig(ParticleKind kind, int baseTarget, float widthFactor, float spawnSpeed, float minLife, float maxLife, float baseSpeed, float randSpeed, float accelY) {
        this.kind = kind;
        this.baseTarget = baseTarget;
        this.widthFactor = widthFactor;
        this.spawnSpeed = spawnSpeed;
        this.minLife = minLife;
        this.maxLife = maxLife;
        this.baseSpeed = baseSpeed;
        this.randSpeed = randSpeed;
        this.accelY = accelY;
    }
}
