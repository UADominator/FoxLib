package foxiwhitee.FoxLib.client.tooltips;

import com.gtnewhorizon.gtnhlib.client.event.RenderTooltipEvent;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TooltipEventHandler {
    private static Field theSlotField;
    private static boolean reflectionInitTried;

    @Optional.Method(modid = "gtnhlib")
    @SubscribeEvent
    public void OnDrawGTNH(RenderTooltipEvent event) {
        if (!TooltipEngine.isShouldRenderVanillaTooltip()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        try {
            boolean result = renderHoveredSlotTooltip(event);
            TooltipEngine.setShouldRenderVanillaTooltip(!result);
        } catch (Throwable ignored) {}
    }

    private static boolean renderHoveredSlotTooltip(GuiScreenEvent.DrawScreenEvent.Post event) {
        long now = System.currentTimeMillis();
        if (TooltipEngine.wasRenderedRecently(now)) {
            return false;
        }

        GuiScreen screen = event.gui;
        if (!(screen instanceof GuiContainer container)) {
            return false;
        }

        Slot hovered = readHoveredSlot(container);
        if (hovered == null) {
            return false;
        }
        ItemStack stack = hovered.getStack();
        if (stack == null) {
            return false;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) {
            return false;
        }

        boolean advanced = mc.gameSettings.advancedItemTooltips;

        List<String> rawLines;
        try {
            rawLines = stack.getTooltip(player, advanced);
        } catch (Throwable tooltipFailure) {
            rawLines = null;
        }
        if (rawLines == null || rawLines.isEmpty()) {
            rawLines = new ArrayList<>(1);
            try {
                rawLines.add(stack.getDisplayName());
            } catch (Throwable ignored) {
                return false;
            }
        }

        List<String> lines = new ArrayList<>(rawLines.size());
        for (int i = 0; i < rawLines.size(); i++) {
            String line = rawLines.get(i);
            if (line == null) {
                continue;
            }
            if (i == 0) {
                line = stack.getRarity().rarityColor + line;
            } else {
                line = EnumChatFormatting.GRAY + line;
            }
            lines.add(line);
        }
        if (lines.isEmpty()) {
            return false;
        }

        TooltipEngine.setHoveredStack(stack);

        int hash = TooltipEngine.createHash(lines, stack);
        if (TooltipEngine.wasRenderedFor(now, hash, event.mouseX, event.mouseY)) {
            return false;
        }
        return TooltipRenderer.render(lines, stack, event.mouseX, event.mouseY, mc.fontRenderer, now, hash);
    }

    @SuppressWarnings("all")
    private static Slot readHoveredSlot(GuiContainer container) {
        if (!reflectionInitTried) {
            reflectionInitTried = true;
            try {
                theSlotField = GuiContainer.class.getDeclaredField("theSlot");
            } catch (NoSuchFieldException primary) {
                try {
                    theSlotField = GuiContainer.class.getDeclaredField("field_147006_u");
                } catch (NoSuchFieldException ignored) {
                    theSlotField = null;
                }
            }
            if (theSlotField != null) {
                theSlotField.setAccessible(true);
            }
        }
        if (theSlotField == null) {
            return null;
        }
        try {
            return (Slot) theSlotField.get(container);
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}
