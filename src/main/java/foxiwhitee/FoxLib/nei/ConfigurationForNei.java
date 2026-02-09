package foxiwhitee.FoxLib.nei;

import codechicken.nei.api.API;
import codechicken.nei.recipe.*;
import foxiwhitee.FoxLib.client.gui.FoxBaseGui;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class ConfigurationForNei {
    private final NeiProcessor processor;
    private boolean doNext = true;

    public ConfigurationForNei(NeiProcessor processor) {
        this.processor = processor;
    }

    public ConfigurationForNei doNextIf(boolean doNext) {
        this.doNext = doNext;
        return this;
    }

    public ConfigurationForNei doNextAlways() {
        this.doNext = true;
        return this;
    }

    public ConfigurationForNei addCatalyst(String handlerId, Block catalyst) {
        return addCatalyst(handlerId, new ItemStack(catalyst), true);
    }

    public ConfigurationForNei addCatalyst(IRecipeHandler handler, Block catalyst) {
        return addCatalyst(handler.getHandlerId(), new ItemStack(catalyst), true);
    }

    public ConfigurationForNei addCatalyst(IRecipeHandler handler, Block catalyst, boolean addIf) {
        return addCatalyst(handler.getHandlerId(), new ItemStack(catalyst), addIf);
    }

    public ConfigurationForNei addCatalyst(String handlerId, Block catalyst, boolean addIf) {
        return addCatalyst(handlerId, new ItemStack(catalyst), addIf);
    }

    public ConfigurationForNei addCatalyst(String handlerId, Item catalyst) {
        return addCatalyst(handlerId, new ItemStack(catalyst), true);
    }

    public ConfigurationForNei addCatalyst(IRecipeHandler handler, Item catalyst) {
        return addCatalyst(handler.getHandlerId(), new ItemStack(catalyst), true);
    }

    public ConfigurationForNei addCatalyst(IRecipeHandler handler, Item catalyst, boolean addIf) {
        return addCatalyst(handler.getHandlerId(), new ItemStack(catalyst), addIf);
    }

    public ConfigurationForNei addCatalyst(String handlerId, Item catalyst, boolean addIf) {
        return addCatalyst(handlerId, new ItemStack(catalyst), addIf);
    }

    public ConfigurationForNei addCatalyst(String handlerId, ItemStack catalyst) {
        return addCatalyst(handlerId, catalyst, true);
    }

    public ConfigurationForNei addCatalyst(IRecipeHandler handler, ItemStack catalyst) {
        return addCatalyst(handler.getHandlerId(), catalyst, true);
    }

    public ConfigurationForNei addCatalyst(IRecipeHandler handler, ItemStack catalyst, boolean addIf) {
        return addCatalyst(handler.getHandlerId(), catalyst, addIf);
    }

    public ConfigurationForNei addCatalyst(String handlerId, ItemStack catalyst, boolean addIf) {
        if (addIf && doNext) {
            API.addRecipeCatalyst(catalyst, handlerId);
        }
        return this;
    }

    public ConfigurationForNei addHandler(IRecipeHandler handler, boolean addIf) {
        if (addIf && doNext) {
            if (handler instanceof IUsageHandler iuh) {
                API.registerUsageHandler(iuh);
            }
            if (handler instanceof ICraftingHandler iuh) {
                API.registerRecipeHandler(iuh);
            }
        }
        return this;
    }

    public ConfigurationForNei addHandler(IRecipeHandler handler) {
        return addHandler(handler, true);
    }

    public ConfigurationForNei addHandlerInfo(String handlerID, String item, String nbt, int width, int height, int recipePerPAge, boolean addIf) {
        if (addIf && doNext) {
            HandlerInfo info = new HandlerInfo(handlerID, processor.modName(), processor.modId(), true, "");
            info.setItem(item, nbt);
            info.setHandlerDimensions(height, width, recipePerPAge);
            GuiRecipeTab.handlerAdderFromIMC.put(info.getHandlerName(), info);
        }
        return this;
    }

    public ConfigurationForNei addHandlerInfo(String handlerID, String item, int width, int height, int recipePerPAge, boolean addIf) {
        return addHandlerInfo(handlerID, item, "", width, height, recipePerPAge, addIf);
    }

    public ConfigurationForNei addHandlerInfo(String handlerID, String item, String nbt, int width, int height, int recipePerPAge) {
        return addHandlerInfo(handlerID, item, nbt, width, height, recipePerPAge, true);
    }

    public ConfigurationForNei addHandlerInfo(String handlerID, String item, int width, int height, int recipePerPAge) {
        return addHandlerInfo(handlerID, item, "", width, height, recipePerPAge, true);
    }

    public ConfigurationForNei addOverlay(Class<? extends FoxBaseGui> gui, String handlerId, boolean addIf) {
        if (addIf && doNext) {
            UniversalOverlayHandler.guis.add(gui);
            API.registerGuiOverlayHandler(gui, new UniversalOverlayHandler(), handlerId);
        }
        return this;
    }

    public ConfigurationForNei addOverlay(Class<? extends FoxBaseGui> gui, IRecipeHandler handler, boolean addIf) {
        return addOverlay(gui, handler.getHandlerId(), addIf);
    }

    public ConfigurationForNei addOverlay(Class<? extends FoxBaseGui> gui, String handlerId) {
        return addOverlay(gui, handlerId, true);
    }

    public ConfigurationForNei addOverlay(Class<? extends FoxBaseGui> gui, IRecipeHandler handler) {
        return addOverlay(gui, handler.getHandlerId(), true);
    }

    public ConfigurationForNei addOverlaySorting(Class<? extends FoxBaseGui> gui, UniversalOverlayHandler.PositionsSort handlerId, boolean addIf) {
        if (addIf && doNext) {
            UniversalOverlayHandler.sorts.put(gui, handlerId);
        }
        return this;
    }

    public ConfigurationForNei addOverlaySorting(Class<? extends FoxBaseGui> gui, UniversalOverlayHandler.PositionsSort handlerId) {
        return addOverlaySorting(gui, handlerId, true);
    }
}
