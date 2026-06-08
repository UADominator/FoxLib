package foxiwhitee.FoxLib.client.tooltips;

import foxiwhitee.FoxLib.client.tooltips.theme.ParticleKind;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class TooltipParticles {

    public static final class Particle {
        public ParticleKind kind;
        public float x, y;
        public float vx, vy;
        public float ax, ay;
        public float life;
        public float maxLife;
        public float size;
        public int color;
        public float rot;
        public float vrot;
        public float seed;
    }

    private static final int MAX_PARTICLES = 220;
    private static final float TICK_FALLBACK_SECONDS = 1.0F / 60.0F;

    private static long lastTickMs = -1L;
    private static final Random RNG = new Random();

    private TooltipParticles() {
    }

    public static float advance(long nowMs) {
        long prev = lastTickMs;
        lastTickMs = nowMs;
        if (prev < 0L) {
            return TICK_FALLBACK_SECONDS;
        }
        float dt = (nowMs - prev) / 1000.0F;
        if (dt < 0.0F) {
            dt = 0.0F;
        }
        if (dt > 0.1F) {
            dt = 0.1F;
        }
        return dt;
    }

    public static void update(List<Particle> particles, float dt) {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.vx += p.ax * dt;
            p.vy += p.ay * dt;
            p.x += p.vx * dt;
            p.y += p.vy * dt;
            p.rot += p.vrot * dt;
            p.life -= dt;
            if (p.life <= 0.0F) {
                it.remove();
            }
        }
    }

    public static void cull(List<Particle> particles, float boxX, float boxY, float boxW, float boxH, float pad) {
        float minX = boxX - pad;
        float maxX = boxX + boxW + pad;
        float minY = boxY - pad;
        float maxY = boxY + boxH + pad;
        particles.removeIf(p -> p.x < minX || p.x > maxX || p.y < minY || p.y > maxY);
    }

    public static int maxPerKey() {
        return MAX_PARTICLES;
    }

    public static Random rng() {
        return RNG;
    }

    public static void spawn(List<Particle> list, ParticleKind kind, float x, float y, float vx, float vy, float ax, float ay, float life, float size, int color) {
        Particle p = new Particle();
        p.kind = kind;
        p.x = x; p.y = y;
        p.vx = vx; p.vy = vy;
        p.ax = ax; p.ay = ay;
        p.life = life; p.maxLife = life;
        p.size = size;
        p.color = color;
        p.rot = 0.0F;
        p.vrot = 0.0F;
        p.seed = RNG.nextFloat();
        list.add(p);
        while (list.size() > MAX_PARTICLES) {
            list.remove(0);
        }
    }
}
