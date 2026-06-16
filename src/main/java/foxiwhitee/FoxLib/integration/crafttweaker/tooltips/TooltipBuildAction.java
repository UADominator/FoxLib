package foxiwhitee.FoxLib.integration.crafttweaker.tooltips;

import foxiwhitee.FoxLib.client.tooltips.theme.ThemeRegister;
import minetweaker.IUndoableAction;
import net.minecraft.nbt.NBTTagCompound;

public class TooltipBuildAction implements IUndoableAction {
    private final ZenTheme theme;
    private final Object target;
    private final BuildType type;
    private final String themeId;
    private final int damage;

    public enum BuildType {
        MOD, ITEM, TEXT, NBT
    }

    public TooltipBuildAction(ZenTheme theme, Object target, BuildType type, int damage) {
        this.theme = theme;
        this.target = target;
        this.type = type;
        this.damage = damage;
        this.themeId = (theme != null && theme.getInternal() != null) ? theme.getInternal().getId() : null;
    }

    @Override
    public void apply() {
        if (this.theme == null || this.theme.getInternal() == null || this.target == null) {
            return;
        }

        switch (this.type) {
            case MOD:
                ZenTooltipManager.BRIDGE.callBuildForMod(this.theme.getInternal(), (String) this.target);
                break;
            case ITEM:
                ZenTooltipManager.BRIDGE.callBuildForItem(this.theme.getInternal(), (String) this.target, damage);
                break;
            case TEXT:
                ZenTooltipManager.BRIDGE.callBuildForText(this.theme.getInternal(), (String) this.target);
                break;
            case NBT:
                ZenTooltipManager.BRIDGE.callBuildForNBT(this.theme.getInternal(), (NBTTagCompound) this.target);
                break;
        }
    }

    @Override
    public boolean canUndo() {
        return this.themeId != null;
    }

    @Override
    public void undo() {
        if (this.themeId != null) {
            ThemeRegister.removeThemeById(this.themeId);
        }
    }

    @Override
    public String describe() {
        return "Applying FoxLib Tooltip Theme: " + this.themeId + " to " + this.target;
    }

    @Override
    public String describeUndo() {
        return "Removing FoxLib Tooltip Theme: " + this.themeId;
    }

    @Override
    public Object getOverrideKey() {
        return null;
    }
}
