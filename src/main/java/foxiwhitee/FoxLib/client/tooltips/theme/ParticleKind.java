package foxiwhitee.FoxLib.client.tooltips.theme;

import foxiwhitee.FoxLib.client.tooltips.TooltipDraw;
import foxiwhitee.FoxLib.client.tooltips.TooltipParticles;

public enum ParticleKind {
    EMBER {
        @Override
        public void draw(TooltipParticles.Particle p, float time) {
            float lifeT = p.life / p.maxLife;
            int c = TooltipDraw.scaleAlpha(p.color, lifeT * lifeT);
            float s = p.size * (0.5F + lifeT * 0.5F);
            TooltipDraw.diamond(p.x, p.y, s, c);
        }
    },
    SPORE {
        @Override
        public void draw(TooltipParticles.Particle p, float time) {
            float lifeT = p.life / p.maxLife;
            float wob = (float) Math.sin(time * 1.6F + p.seed * 8.0F) * 0.7F;
            int c = TooltipDraw.scaleAlpha(p.color, lifeT * 0.85F);
            TooltipDraw.circle(p.x + wob, p.y, p.size * (0.6F + lifeT * 0.4F), c);
        }
    },
    STAR {
        @Override
        public void draw(TooltipParticles.Particle p, float time) {
            float lifeT = p.life / p.maxLife;
            float twinkle = (float) Math.sin(time * 5.0F + p.seed * 12.0F);
            float vis = TooltipDraw.clamp01(twinkle * 0.5F + 0.5F);
            int c = TooltipDraw.scaleAlpha(p.color, lifeT * vis);
            TooltipDraw.diamond(p.x, p.y, p.size * (0.5F + lifeT * 0.5F), c);
        }
    },
    SPARK {
        @Override
        public void draw(TooltipParticles.Particle p, float time) {
            float lifeT = p.life / p.maxLife;
            int c = TooltipDraw.scaleAlpha(p.color, lifeT);
            TooltipDraw.thickLine(p.x, p.y, p.x - p.vx * 0.04F, p.y - p.vy * 0.04F, 1.0F, c);
        }
    };

    public abstract void draw(TooltipParticles.Particle p, float time);
}
