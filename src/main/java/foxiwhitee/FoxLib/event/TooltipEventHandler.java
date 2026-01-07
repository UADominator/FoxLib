package foxiwhitee.FoxLib.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import foxiwhitee.FoxLib.utils.helpers.ProductivityBlackListHelper;
import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class TooltipEventHandler {

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (event.itemStack == null) return;
        String value = ProductivityBlackListHelper.getRestrictionStatus(event.itemStack);
        switch (value.toLowerCase()) {
            case "black":
                event.toolTip.add(StatCollector.translateToLocal("tooltip.blacklist"));
                break;
            case "white":
                event.toolTip.add(StatCollector.translateToLocal("tooltip.whitelist"));
        }
    }
}
