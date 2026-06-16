package foxiwhitee.FoxLib.client.tooltips;

import foxiwhitee.FoxLib.client.tooltips.theme.elements.attribute.AttributeFilter;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.attribute.AttributeIcons;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.attribute.AttributeInfo;
import foxiwhitee.FoxLib.client.tooltips.theme.*;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.FrameRenderer;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.FrameStyle;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.frame.ImageFrame;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.lines.GlowOutlineConfig;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.lines.InnerLineConfig;
import foxiwhitee.FoxLib.client.tooltips.theme.elements.separator.SeparatorData;
import foxiwhitee.FoxLib.config.FoxLibConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TooltipRenderer {
    private static final RenderItem ITEM_RENDERER = new RenderItem();
    private static final int PADDING = 10;
    private static final int APPEAR_DURATION_MS = 150;
    private static final float INF_SCALE = 1.0F;
    private static final String INF = "∞";
    private static int cachedDisplayW = -1;
    private static int cachedDisplayH = -1;
    private static ScaledResolution cachedResolution;

    public static boolean render(List<String> lines, ItemStack stack, int mouseX, int mouseY, FontRenderer fontRenderer, long now, int hash) {
        if (lines == null || lines.isEmpty() || fontRenderer == null) {
            return false;
        }
        if (!FoxLibConfig.enableCustomTooltips) {
            return false;
        }

        List<String> originalLines = lines;
        lines = AttributeFilter.stripVanillaAttributes(stack, lines);
        if (lines.isEmpty()) {
            return false;
        }

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = getResolution(mc);

        int maxLineWidth = resolution.getScaledWidth() - 32;
        lines = wrapLines(lines, fontRenderer, maxLineWidth);
        TooltipTheme theme = ThemeRegister.findTheme(stack, lines);
        if (theme == null) {
            return false;
        }
        boolean hasIcon = stack != null && theme.isDrawIcon();
        int iconSize = hasIcon ? 18 : 0;
        int iconOffset = hasIcon ? iconSize + 6 : 0;

        int textWidth = 0;
        for (String line : lines) {
            textWidth = Math.max(textWidth, fontRenderer.getStringWidth(line));
        }

        int textHeight = 8 + Math.max(0, lines.size() - 1) * 11;
        if (lines.size() > 1 && theme.getSeparatorConfig() != null) {
            textHeight += 6 * Math.min(lines.size() - 1, theme.getSeparatorConfig().getSeparators().size());
        }

        List<AttributeInfo.Attr> attrs = AttributeInfo.compute(stack, lines, originalLines);
        int attrRowWidth = computeAttrRowWidth(attrs, fontRenderer);
        int attrRowHeight = attrs.isEmpty() ? 0 : (AttributeIcons.ICON_SIZE + 4);
        int contentWidth = Math.max(textWidth, attrRowWidth) + iconOffset;
        int contentHeight = Math.max(textHeight, iconSize) + attrRowHeight;

        int boxWidth = contentWidth + PADDING * 2;
        int boxHeight = contentHeight + PADDING * 2;

        float overflowScale = 1.0F;
        int screenW = resolution.getScaledWidth();
        int screenH = resolution.getScaledHeight();
        int maxBoxW = (int) (screenW * 0.8F);
        int maxBoxH = (int) (screenH * 0.9F);
        if (boxWidth > maxBoxW) {
            overflowScale = Math.min(overflowScale, maxBoxW / (float) boxWidth);
        }
        if (boxHeight > maxBoxH) {
            overflowScale = Math.min(overflowScale, maxBoxH / (float) boxHeight);
        }
        if (overflowScale < 0.55F) overflowScale = 0.55F;

        int effectiveW = Math.round(boxWidth * overflowScale);
        int effectiveH = Math.round(boxHeight * overflowScale);

        int targetX = mouseX + 12;
        int targetY = mouseY - 12;

        if (targetX + effectiveW > screenW) {
            targetX = mouseX - 16 - effectiveW;
        }
        if (targetX < 4) {
            targetX = 4;
        }
        if (targetX + effectiveW > screenW - 4) {
            targetX = screenW - effectiveW - 4;
        }
        if (targetY + effectiveH > screenH) {
            targetY = mouseY - effectiveH - 12;
        }
        if (targetY < 4) {
            targetY = 4;
        }
        if (targetY + effectiveH > screenH - 4) {
            targetY = screenH - effectiveH - 4;
        }

        float speed = (float) FoxLibConfig.animationSpeed;
        if (speed <= 0F) speed = 1F;
        int effectiveAppearMs = Math.max(1, Math.round(APPEAR_DURATION_MS / speed));

        TooltipState state = TooltipState.get(now, hash, effectiveAppearMs, theme);
        float dt = OuterHalo.advance(now);

        float rawX = targetX;
        float rawY = targetY;

        float appearProgress = TooltipDraw.clamp01((now - state.hoverStartMs) / (float) effectiveAppearMs);
        float scaleAppear = TooltipDraw.easeOutCubic(appearProgress);
        float alphaAppear = TooltipDraw.easeOutCubic(appearProgress);

        float t = (float) ((now % 100000000L) / 1000.0D);
        float pulse = naturalPulse(t);
        float scale = (0.82F + scaleAppear * 0.18F) * overflowScale;
        float slideY = (1.0F - alphaAppear) * 4.0F;

        float overflowShiftX = boxWidth * (1.0F - overflowScale) / 2.0F;
        float overflowShiftY = boxHeight * (1.0F - overflowScale) / 2.0F;
        int snappedX = Math.round(rawX - overflowShiftX);
        int snappedY = Math.round(rawY - overflowShiftY);
        float pivotX = (float) snappedX + boxWidth * 0.5F;
        float pivotY = (float) snappedY + boxHeight * 0.5F;

        ThemePalette palette = ThemePalette.make(theme, pulse, alphaAppear);

        GL11.glPushMatrix();
        if (slideY != 0F) GL11.glTranslatef(0F, slideY, 0F);
        GL11.glTranslatef(pivotX, pivotY, 0.0F);
        GL11.glScalef(scale, scale, 1.0F);
        GL11.glTranslatef(-pivotX, -pivotY, 0.0F);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        if (theme.isEnableShadow()) {
            float glowPulse = 0.85F + 0.4F * pulse;
            for (int i = 1; i <= 6; i++) {
                float layerAlpha = (0.65F / (i + 0.4F)) * glowPulse;
                int c = TooltipDraw.scaleAlpha(palette.glow, layerAlpha * alphaAppear);
                TooltipDraw.rect((float) snappedX - i, (float) snappedY - i, (float) snappedX + boxWidth + i, (float) snappedY + boxHeight + i, c);
            }
        }
        if (theme.isEnableParticles()) {
            OuterHalo.update(dt, (float) snappedX, (float) snappedY, boxWidth, boxHeight, palette, alphaAppear);
        }

        GlowOutlineConfig glowOutlineConfig = theme.getGlowOutlineConfig();
        if (glowOutlineConfig != null) {
            FrameRenderer.drawGlowOutline(snappedX, snappedY, boxWidth, boxHeight, glowOutlineConfig.glowSize, glowOutlineConfig.animationSpeed, glowOutlineConfig.colors);
        }

        FrameRenderer.drawNineSlice(theme, (float) snappedX, (float) snappedY, boxWidth, boxHeight, alphaAppear);

        InnerLineConfig innerLineConfig = theme.getInnerLineConfig();
        if (innerLineConfig != null) {
            FrameRenderer.drawInnerLine(snappedX, snappedY, boxWidth, boxHeight, innerLineConfig.animationSpeed, innerLineConfig.colors);
        }
        if (theme.getFrame() != null) {
            drawTextureFrame((float) snappedX, (float) snappedY, boxWidth, boxHeight, alphaAppear, theme.getFrame());
        }

        int textX = (int) ((float) snappedX + PADDING + iconOffset);
        int textY = (int) ((float) snappedY + PADDING + 1);

        if (hasIcon) {
            float iconX = (float) snappedX + PADDING;
            float iconY = (float) snappedY + PADDING;
            drawItemSlotFrame(iconX, iconY, theme, alphaAppear);
            float visualIconX = pivotX + scale * (iconX - pivotX);
            float visualIconY = pivotY + scale * (iconY - pivotY);
            GL11.glPushMatrix();
            GL11.glTranslatef(pivotX, pivotY, 0F);
            GL11.glScalef(1F / scale, 1F / scale, 1F);
            GL11.glTranslatef(-pivotX, -pivotY, 0F);
            drawItemIcon(stack, fontRenderer, Math.round(visualIconX), Math.round(visualIconY));
            GL11.glPopMatrix();
        }

        if (theme.getSeparatorConfig() == null || theme.getSeparatorConfig().getSeparators().isEmpty()) {
            for (int i = 0; i < lines.size(); i++) {
                fontRenderer.drawStringWithShadow(lines.get(i), textX, textY, 0xFFFFFFFF);
                textY += (i == 0) ? 10 : 11;
            }
        } else {
            int textPosition = 0;
            SeparatorData last = null;
            for (SeparatorData s : theme.getSeparatorConfig().getSeparators()) {
                if (s.position == -1) {
                    last = s;
                    continue;
                }
                int needLine = s.position + 1;
                if (needLine >= lines.size()) {
                    break;
                }
                while (textPosition != needLine) {
                    fontRenderer.drawStringWithShadow(lines.get(textPosition), textX, textY, 0xFFFFFFFF);
                    textY += (textPosition == 0) ? 10 : 11;
                    textPosition++;
                }
                textY += 4;
                float lineWidth = contentWidth - iconOffset - 4;
                drawHorizontalSeparator(textX + 2, textY - 2, lineWidth, alphaAppear, s.colors);

                textY += 3;
            }
            for (; textPosition < lines.size(); textPosition++) {
                fontRenderer.drawStringWithShadow(lines.get(textPosition), textX, textY, 0xFFFFFFFF);
                textY += (textPosition == 0) ? 10 : 11;
            }
            if (last != null) {
                textY += 4;
                float lineWidth = contentWidth - iconOffset - 4;
                drawHorizontalSeparator(textX + 2, textY - 2, lineWidth, alphaAppear, last.colors);
                boxHeight += 4;
            }
        }

        if (!attrs.isEmpty()) {
            float attrY = (float) snappedY + boxHeight - PADDING - AttributeIcons.ICON_SIZE;
            float attrX = (float) snappedX + PADDING + iconOffset;
            float fadePos = TooltipDraw.clamp01(alphaAppear * 1.5F - lines.size() * 0.06F);
            float rowAlpha = TooltipDraw.smootherStep(fadePos);
            drawAttributeRow(attrs, attrX, attrY, fontRenderer, rowAlpha);
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        return true;
    }

    private static ScaledResolution getResolution(Minecraft mc) {
        if (mc.displayWidth != cachedDisplayW || mc.displayHeight != cachedDisplayH || cachedResolution == null) {
            cachedResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            cachedDisplayW = mc.displayWidth;
            cachedDisplayH = mc.displayHeight;
        }
        return cachedResolution;
    }

    private static List<String> wrapLines(List<String> lines, FontRenderer fontRenderer, int maxWidth) {
        if (maxWidth <= 60) {
            return lines;
        }

        int n = lines.size();
        int[] widths = new int[n];
        boolean anyOver = false;
        for (int i = 0; i < n; i++) {
            widths[i] = fontRenderer.getStringWidth(lines.get(i));
            if (widths[i] > maxWidth) {
                anyOver = true;
            }
        }
        if (!anyOver) {
            return lines;
        }

        List<String> out = new ArrayList<>(n + 4);
        for (int i = 0; i < n; i++) {
            String line = lines.get(i);
            if (widths[i] <= maxWidth) {
                out.add(line);
            } else {
                List<String> wrapped = fontRenderer.listFormattedStringToWidth(line, maxWidth);
                for (Object w : wrapped) {
                    out.add(String.valueOf(w));
                }
            }
        }
        return out;
    }

    private static int computeAttrRowWidth(List<AttributeInfo.Attr> attrs, FontRenderer fontRenderer) {
        if (attrs.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (int i = 0; i < attrs.size(); i++) {
            String v = attrs.get(i).value;
            int textW = INF.equals(v) ? Math.round(fontRenderer.getStringWidth(v) * INF_SCALE) : fontRenderer.getStringWidth(v);
            total += AttributeIcons.ICON_SIZE + 2 + textW;
            if (i < attrs.size() - 1) {
                total += 6;
            }
        }
        return total;
    }

    private static float naturalPulse(float t) {
        float wave1 = (float) Math.sin(t * 1.25F);
        float wave2 = (float) Math.sin(t * 2.40F + 1.3F);
        float wave3 = (float) Math.sin(t * 0.55F + 2.2F);
        return TooltipDraw.clamp01(0.5F + 0.22F * wave1 + 0.16F * wave2 + 0.12F * wave3);
    }

    private static void drawItemSlotFrame(float x, float y, TooltipTheme theme, float alphaMul) {
        FrameStyle s = theme.getStyle();
        int outer = (s.borderOuter.getRGB() & 0x00FFFFFF) | ((int) (220 * alphaMul) << 24);
        int innerFill = (0x000000) | ((int) (180 * alphaMul) << 24);
        int sheen = (0xFFFFFF) | ((int) (110 * alphaMul) << 24);

        float pad = 1F;
        float left = x - pad;
        float top = y - pad;
        float right = x + 16 + pad;
        float bottom = y + 16 + pad;

        TooltipDraw.rect(left - 1, top - 1, right + 1, top, outer);
        TooltipDraw.rect(left - 1, bottom, right + 1, bottom + 1, outer);
        TooltipDraw.rect(left - 1, top, left, bottom, outer);
        TooltipDraw.rect(right, top, right + 1, bottom, outer);

        TooltipDraw.rect(left, top, right, bottom, innerFill);

        TooltipDraw.rect(left, top, right, top + 0.5F, sheen);
        TooltipDraw.rect(left, top, left + 0.5F, bottom, sheen);
    }

    private static void drawItemIcon(ItemStack stack, FontRenderer fontRenderer, float x, float y) {
        Minecraft mc = Minecraft.getMinecraft();

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        float prevZ = ITEM_RENDERER.zLevel;
        ITEM_RENDERER.zLevel = 400.0F;
        try {
            ITEM_RENDERER.renderItemAndEffectIntoGUI(fontRenderer, mc.getTextureManager(), stack, (int) x, (int) y);
            ItemStack overlayStack = stack;
            if (stack.stackSize > 1) {
                overlayStack = stack.copy();
                overlayStack.stackSize = 1;
            }
            ITEM_RENDERER.renderItemOverlayIntoGUI(fontRenderer, mc.getTextureManager(), overlayStack, (int) x, (int) y);
        } finally {
            ITEM_RENDERER.zLevel = prevZ;
        }

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private static void drawAttributeRow(java.util.List<AttributeInfo.Attr> attrs, float x, float y, FontRenderer fontRenderer, float rowAlpha) {
        if (rowAlpha <= 0F) return;
        int alphaInt = TooltipDraw.clampInt((int) (255 * rowAlpha), 0, 255);
        float cursor = x;
        for (AttributeInfo.Attr attr : attrs) {
            AttributeIcons.draw(attr.kind, cursor, y, rowAlpha);
            cursor += AttributeIcons.ICON_SIZE + 2;
            int color = (alphaInt << 24) | (attr.color & 0x00FFFFFF);
            int textY = (int) (y + (AttributeIcons.ICON_SIZE - 8) / 2F);
            if (INF.equals(attr.value)) {
                float pivotX = cursor;
                float pivotY = textY + 4F;
                GL11.glPushMatrix();
                GL11.glTranslatef(pivotX, pivotY, 0F);
                GL11.glScalef(INF_SCALE, INF_SCALE, 1F);
                GL11.glTranslatef(-pivotX, -pivotY, 0F);
                fontRenderer.drawStringWithShadow(INF, (int) cursor, textY, color);
                GL11.glPopMatrix();
                cursor += fontRenderer.getStringWidth(INF) * INF_SCALE + 6;
            } else {
                fontRenderer.drawStringWithShadow(attr.value, (int) cursor, textY, color);
                cursor += fontRenderer.getStringWidth(attr.value) + 6;
            }
        }
    }

    private static void drawHorizontalSeparator(float x, float y, float width, float alphaMul, List<Color> colors) {
        if (alphaMul <= 0F || colors == null || colors.isEmpty()) return;

        float lineThickness = 1.15F;
        float baseAlphaMul = 0.8F * alphaMul;

        Tessellator tessellator = Tessellator.instance;

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        long time = System.currentTimeMillis();
        int colorCount = colors.size();

        int c1_rgb, c2_rgb;

        if (colorCount == 1) {
            c1_rgb = colors.get(0).getRGB();
            c2_rgb = c1_rgb;
        } else {
            float speed = 0.001F;
            float globalPhase = (time % 1000000) * speed;

            int index1 = (int) globalPhase % colorCount;
            int index2 = (index1 + 1) % colorCount;
            float colorRatio = globalPhase % 1.0F;

            c1_rgb = getInterpolatedColorRGB(colors.get(index1), colors.get(index2), colorRatio);
            c2_rgb = getInterpolatedColorRGB(colors.get(index2), colors.get((index2 + 1) % colorCount), colorRatio);
        }

        float fadeW = width * 0.15F;
        float centerW = width - (fadeW * 2);

        float x2 = x + fadeW;
        float x3 = x + fadeW + centerW;
        float x4 = x + width;

        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(0, 0, 0, 0);
        tessellator.addVertex(x, y, 0.0D);
        tessellator.addVertex(x, y + lineThickness, 0.0D);

        int aCenter1 = (int) (220 * baseAlphaMul);
        tessellator.setColorRGBA((c1_rgb >> 16) & 255, (c1_rgb >> 8) & 255, c1_rgb & 255, aCenter1);
        tessellator.addVertex(x2, y + lineThickness, 0.0D);
        tessellator.addVertex(x2, y, 0.0D);
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setColorRGBA((c1_rgb >> 16) & 255, (c1_rgb >> 8) & 255, c1_rgb & 255, aCenter1);
        tessellator.addVertex(x2, y, 0.0D);
        tessellator.addVertex(x2, y + lineThickness, 0.0D);

        int aCenter2 = (int) (220 * baseAlphaMul);
        tessellator.setColorRGBA((c2_rgb >> 16) & 255, (c2_rgb >> 8) & 255, c2_rgb & 255, aCenter2);
        tessellator.addVertex(x3, y + lineThickness, 0.0D);
        tessellator.addVertex(x3, y, 0.0D);
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setColorRGBA((c2_rgb >> 16) & 255, (c2_rgb >> 8) & 255, c2_rgb & 255, aCenter2);
        tessellator.addVertex(x3, y, 0.0D);
        tessellator.addVertex(x3, y + lineThickness, 0.0D);

        tessellator.setColorRGBA(0, 0, 0, 0);
        tessellator.addVertex(x4, y + lineThickness, 0.0D);
        tessellator.addVertex(x4, y, 0.0D);
        tessellator.draw();

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glPopAttrib();
    }

    private static int getInterpolatedColorRGB(Color c1, Color c2, float ratio) {
        float r = c1.getRed() * (1 - ratio) + c2.getRed() * ratio;
        float g = c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio;
        float b = c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio;
        return new Color((int)r, (int)g, (int)b).getRGB();
    }

    public static void drawTextureFrame(float x, float y, float width, float height, float alpha, ImageFrame imageFrame) {
        int realCenterWidth = imageFrame.realCenterWidth;
        ResourceLocation texture = imageFrame.texture;
        float baseTextureWidth = imageFrame.width;

        final float baseTextureHeight = 32.0F;
        final float baseCornerWidth = 16.0F;
        final float baseCornerHeight = 16.0F;
        final float baseMaxSideWidth = 96.0F;
        final float baseSideHeight = 16.0F;
        final float centerTopYShift = -2.0F;
        final float centerBottomYShift = 2.0F;
        final float offsetLeft = -10.0F;
        final float offsetRight = 10.0F;
        final float offsetTop = -10.0F;
        final float offsetBottom = 10.0F;

        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(texture);

        int textureId = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        float uCornerL = baseCornerWidth / baseTextureWidth;
        float uCornerR = 1.0F - (baseCornerWidth / baseTextureWidth);
        float vCornerT = baseCornerHeight / baseTextureHeight;
        float vCornerB = 1.0F - (baseCornerHeight / baseTextureHeight);

        float cornerLeftOuterX  = x + offsetLeft;
        float cornerRightOuterX = x + width + offsetRight;

        float cornerLeftInnerX  = cornerLeftOuterX + baseCornerWidth;
        float cornerRightInnerX = cornerRightOuterX - baseCornerWidth;

        float posY0 = y + offsetTop;
        float posY3 = y + height + offsetBottom;
        float posY1 = posY0 + baseCornerHeight;
        float posY2 = posY3 - baseCornerHeight;

        float cleanAvailableSpace = cornerRightInnerX - cornerLeftInnerX;

        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);

        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(cornerLeftOuterX, posY1, 0.0D, 0.0F, vCornerT);
        tessellator.addVertexWithUV(cornerLeftInnerX, posY1, 0.0D, uCornerL, vCornerT);
        tessellator.addVertexWithUV(cornerLeftInnerX, posY0, 0.0D, uCornerL, 0.0F);
        tessellator.addVertexWithUV(cornerLeftOuterX, posY0, 0.0D, 0.0F, 0.0F);

        tessellator.addVertexWithUV(cornerRightInnerX, posY1, 0.0D, uCornerR, vCornerT);
        tessellator.addVertexWithUV(cornerRightOuterX, posY1, 0.0D, 1.0F, vCornerT);
        tessellator.addVertexWithUV(cornerRightOuterX, posY0, 0.0D, 1.0F, 0.0F);
        tessellator.addVertexWithUV(cornerRightInnerX, posY0, 0.0D, uCornerR, 0.0F);

        tessellator.addVertexWithUV(cornerLeftOuterX, posY3, 0.0D, 0.0F, 1.0F);
        tessellator.addVertexWithUV(cornerLeftInnerX, posY3, 0.0D, uCornerL, 1.0F);
        tessellator.addVertexWithUV(cornerLeftInnerX, posY2, 0.0D, uCornerL, vCornerB);
        tessellator.addVertexWithUV(cornerLeftOuterX, posY2, 0.0D, 0.0F, vCornerB);

        tessellator.addVertexWithUV(cornerRightInnerX, posY3, 0.0D, uCornerR, 1.0F);
        tessellator.addVertexWithUV(cornerRightOuterX, posY3, 0.0D, 1.0F, 1.0F);
        tessellator.addVertexWithUV(cornerRightOuterX, posY2, 0.0D, 1.0F, vCornerB);
        tessellator.addVertexWithUV(cornerRightInnerX, posY2, 0.0D, uCornerR, vCornerB);
        tessellator.draw();

        if (cleanAvailableSpace >= realCenterWidth) {
            float pctRealSideW = realCenterWidth / baseTextureWidth;
            float pctCornerW = baseCornerWidth / baseTextureWidth;
            float pctMaxSideW = baseMaxSideWidth / baseTextureWidth;

            float pctSideStart = pctCornerW + ((pctMaxSideW - pctRealSideW) / 2.0F);
            float pctSideEnd = pctSideStart + pctRealSideW;

            float centerX = x + (width / 2.0F);
            float midXStart = centerX - (realCenterWidth / 2.0F);
            float midXEnd = centerX + (realCenterWidth / 2.0F);

            tessellator.startDrawingQuads();

            float topY0 = posY0 + centerTopYShift;
            float topY1 = posY0 + baseSideHeight + centerTopYShift;

            tessellator.addVertexWithUV(midXStart, topY1, 0.0D, pctSideStart, vCornerT);
            tessellator.addVertexWithUV(midXEnd, topY1, 0.0D, pctSideEnd,   vCornerT);
            tessellator.addVertexWithUV(midXEnd, topY0, 0.0D, pctSideEnd,   0.0F);
            tessellator.addVertexWithUV(midXStart, topY0, 0.0D, pctSideStart, 0.0F);

            float botY2 = posY3 - baseSideHeight + centerBottomYShift;
            float botY3 = posY3 + centerBottomYShift;

            tessellator.addVertexWithUV(midXStart, botY3, 0.0D, pctSideStart, 1.0F);
            tessellator.addVertexWithUV(midXEnd, botY3, 0.0D, pctSideEnd,   1.0F);
            tessellator.addVertexWithUV(midXEnd, botY2, 0.0D, pctSideEnd,   vCornerB);
            tessellator.addVertexWithUV(midXStart, botY2, 0.0D, pctSideStart, vCornerB);

            tessellator.draw();
        }
    }

}
