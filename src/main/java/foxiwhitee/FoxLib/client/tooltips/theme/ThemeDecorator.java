package foxiwhitee.FoxLib.client.tooltips.theme;

import foxiwhitee.FoxLib.client.tooltips.TooltipDraw;
import foxiwhitee.FoxLib.client.tooltips.TooltipParticles;

import java.util.List;
import java.util.Random;

public class ThemeDecorator {
    private static final float SPAWN_OFFSET = 2F;
    public static void emitAndDraw(TooltipTheme theme, ThemePalette palette, List<TooltipParticles.Particle> particles, float dt, float boxX, float boxY, float boxW, float boxH, float time, float appear, boolean particlesEnabled) {
        if (!particlesEnabled) {
            return;
        }

        TooltipParticles.update(particles, dt);
        TooltipParticles.cull(particles, boxX, boxY, boxW, boxH, 60F);

        ParticleConfig config = theme.getFx();
        if (config == null) {
            return;
        }

        spawnParticlesOutside(config, palette, particles, dt, boxX, boxY, boxW, boxH, appear);

        for (TooltipParticles.Particle pp : particles) {
            pp.kind.draw(pp, time);
        }
    }

    private static void spawnParticlesOutside(
        ParticleConfig fx,
        ThemePalette p,
        List<TooltipParticles.Particle> ps,
        float dt,
        float bx,
        float by,
        float bw,
        float bh,
        float appear) {

        Random rng = TooltipParticles.rng();

        int target = (int) (fx.baseTarget + bw * fx.widthFactor);
        if (target > TooltipParticles.maxPerKey() - 4) {
            target = TooltipParticles.maxPerKey() - 4;
        }

        int spawn = Math.max(1, (int) (fx.spawnSpeed * dt * (0.4F + appear * 0.6F)));

        for (int i = 0; i < spawn && ps.size() < target; i++) {
            int side = rng.nextInt(4);
            float ex, ey, vx, vy, ay;
            float push = fx.baseSpeed + rng.nextFloat() * fx.randSpeed;

            if (side == 0) {
                ex = bx + rng.nextFloat() * bw;
                ey = by - SPAWN_OFFSET;
                vx = (rng.nextFloat() - 0.5F) * 8F;
                vy = -push;
                ay = fx.accelY;
            } else if (side == 1) {
                ex = bx + rng.nextFloat() * bw;
                ey = by + bh + SPAWN_OFFSET;
                vx = (rng.nextFloat() - 0.5F) * 8F;
                vy = push;
                ay = -fx.accelY;
            } else if (side == 2) {
                ex = bx - SPAWN_OFFSET;
                ey = by + rng.nextFloat() * bh;
                vx = -push;
                vy = -4F + rng.nextFloat() * 4F;
                ay = fx.accelY * 0.5F;
            } else {
                ex = bx + bw + SPAWN_OFFSET;
                ey = by + rng.nextFloat() * bh;
                vx = push;
                vy = -4F + rng.nextFloat() * 4F;
                ay = fx.accelY * 0.5F;
            }

            float life = fx.minLife + rng.nextFloat() * fx.maxLife;
            int color = TooltipDraw.lerpColor(p.borderBright, p.decorA, rng.nextFloat());

            TooltipParticles.spawn(ps, fx.kind, ex, ey, vx, vy, 0, ay, life, 1.2F + rng.nextFloat(), color);
        }
    }
}
