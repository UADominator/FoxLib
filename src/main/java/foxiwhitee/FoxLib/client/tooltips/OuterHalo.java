package foxiwhitee.FoxLib.client.tooltips;

import foxiwhitee.FoxLib.client.tooltips.theme.ThemePalette;

import java.util.*;

public final class OuterHalo {
    public static final class Dot {
        float x, y, vx, vy, life, maxLife, size;
        int color;
    }

    private static final List<Dot> GLOBAL_POOL = new ArrayList<>();
    private static final ArrayDeque<Dot> FREE_LIST = new ArrayDeque<>(96);
    private static final Random RNG = new Random();

    private static Dot acquireDot() {
        Dot d = FREE_LIST.pollLast();
        return d != null ? d : new Dot();
    }

    private static void releaseDot(Dot d) {
        if (FREE_LIST.size() < 96) {
            FREE_LIST.addLast(d);
        }
    }

    private OuterHalo() {}

    public static void update(float dt, float boxX, float boxY, float boxW, float boxH, ThemePalette palette, float alphaMul) {
        List<Dot> dots = GLOBAL_POOL;

        float cullPad = 60F;
        float minX = boxX - cullPad;
        float maxX = boxX + boxW + cullPad;
        float minY = boxY - cullPad;
        float maxY = boxY + boxH + cullPad;

        Iterator<Dot> it = dots.iterator();
        while (it.hasNext()) {
            Dot d = it.next();
            d.x += d.vx * dt;
            d.y += d.vy * dt;
            d.life -= dt;
            if (d.life <= 0F || d.x < minX || d.x > maxX || d.y < minY || d.y > maxY) {
                it.remove();
                releaseDot(d);
            }
        }

        int target = (int) (28 + (boxW + boxH) * 0.25F);
        if (target > 80) {
            target = 80;
        }
        int spawn = Math.max(1, (int) (40F * dt));
        for (int i = 0; i < spawn && dots.size() < target; i++) {
            spawnDot(dots, boxX, boxY, boxW, boxH, palette);
        }

        for (Dot d : dots) {
            float lifeT = d.life / d.maxLife;
            float fade = (float) Math.sin(lifeT * Math.PI);
            int alpha = (int) (255 * fade * alphaMul * 0.85F);
            int c = (TooltipDraw.clampInt(alpha, 0, 255) << 24) | (d.color & 0x00FFFFFF);
            TooltipDraw.rect(d.x - 0.5F, d.y - 0.5F, d.x + 1F, d.y + 1F, c);
        }
    }

    private static void spawnDot(List<Dot> list, float bx, float by, float bw, float bh, ThemePalette palette) {
        float gap = 4F + RNG.nextFloat() * 14F;
        int side = RNG.nextInt(4);
        Dot d = acquireDot();
        d.maxLife = 0.9F + RNG.nextFloat() * 1.6F;
        d.life = d.maxLife;
        d.size = 0.8F + RNG.nextFloat() * 0.7F;
        d.color = RNG.nextFloat() < 0.55F ? palette.borderBright : palette.accent;

        if (side == 0) {
            d.x = bx + RNG.nextFloat() * bw;
            d.y = by - gap;
            d.vx = (RNG.nextFloat() - 0.5F) * 4F;
            d.vy = -2F - RNG.nextFloat() * 3F;
        } else if (side == 1) {
            d.x = bx + RNG.nextFloat() * bw;
            d.y = by + bh + gap;
            d.vx = (RNG.nextFloat() - 0.5F) * 4F;
            d.vy = 2F + RNG.nextFloat() * 3F;
        } else if (side == 2) {
            d.x = bx - gap;
            d.y = by + RNG.nextFloat() * bh;
            d.vx = -2F - RNG.nextFloat() * 3F;
            d.vy = (RNG.nextFloat() - 0.5F) * 4F;
        } else {
            d.x = bx + bw + gap;
            d.y = by + RNG.nextFloat() * bh;
            d.vx = 2F + RNG.nextFloat() * 3F;
            d.vy = (RNG.nextFloat() - 0.5F) * 4F;
        }
        list.add(d);
    }
}
