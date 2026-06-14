package foxiwhitee.FoxLib.asm.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.List;

@SuppressWarnings("unused")
public class NeiTooltipTransformer implements IClassTransformer {
    private static final String TARGET = "codechicken.nei.guihook.GuiContainerManager";

    private static final String HOOK_OWNER = "foxiwhitee/FoxLib/client/tooltips/TooltipEngine";
    private static final String HOOK_RENDER = "renderNeiTooltip";
    private static final String HOOK_RENDER_DESC = "(IILjava/util/List;)Z";
    private static final String HOOK_STACK = "setHoveredStack";
    private static final String HOOK_STACK_DESC = "(Ljava/lang/Object;)V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return null;
        }
        if (!TARGET.equals(transformedName)) {
            return basicClass;
        }

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        boolean changed = false;
        for (MethodNode method : classNode.methods) {
            if (isRenderToolTipMethod(method)) {
                injectRenderHook(method);
                changed = true;
            } else if (isItemTooltipMethod(method)) {
                injectStackHook(method);
                changed = true;
            }
        }

        if (!changed) {
            return basicClass;
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static boolean isRenderToolTipMethod(MethodNode method) {
        boolean nameOk = "renderToolTip".equals(method.name)
            || "drawPagedTooltip".equals(method.name)
            || "drawHoveringText".equals(method.name);
        if (!nameOk) {
            return false;
        }
        if (!Type.VOID_TYPE.equals(Type.getReturnType(method.desc))) {
            return false;
        }
        Type[] args = Type.getArgumentTypes(method.desc);
        int intCount = 0;
        boolean hasList = false;
        for (Type a : args) {
            if (Type.INT_TYPE.equals(a)) {
                intCount++;
            } else if (Type.getType(List.class).equals(a)) {
                hasList = true;
            }
        }
        return intCount >= 2 && hasList;
    }

    private static boolean isItemTooltipMethod(MethodNode method) {
        if (!"drawItemTooltip".equals(method.name)) {
            return false;
        }
        Type[] args = Type.getArgumentTypes(method.desc);
        for (Type arg : args) {
            if ("net/minecraft/item/ItemStack".equals(arg.getInternalName())) {
                return true;
            }
        }
        return false;
    }

    private static void injectRenderHook(MethodNode method) {
        boolean isStatic = (method.access & Opcodes.ACC_STATIC) != 0;
        Type[] args = Type.getArgumentTypes(method.desc);
        int slot = isStatic ? 0 : 1;
        int xSlot = -1, ySlot = -1, listSlot = -1;
        for (Type arg : args) {
            if (Type.INT_TYPE.equals(arg)) {
                if (xSlot < 0) xSlot = slot;
                else if (ySlot < 0) ySlot = slot;
            } else if (Type.getType(List.class).equals(arg)) {
                listSlot = slot;
            }
            slot += arg.getSize();
        }
        if (xSlot < 0 || ySlot < 0 || listSlot < 0) {
            return;
        }

        InsnList hook = new InsnList();
        LabelNode cont = new LabelNode();
        hook.add(new VarInsnNode(Opcodes.ILOAD, xSlot));
        hook.add(new VarInsnNode(Opcodes.ILOAD, ySlot));
        hook.add(new VarInsnNode(Opcodes.ALOAD, listSlot));
        hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK_OWNER, HOOK_RENDER, HOOK_RENDER_DESC, false));
        hook.add(new JumpInsnNode(Opcodes.IFEQ, cont));
        hook.add(new InsnNode(Opcodes.RETURN));
        hook.add(cont);
        method.instructions.insert(hook);
    }

    private static void injectStackHook(MethodNode method) {
        boolean isStatic = (method.access & Opcodes.ACC_STATIC) != 0;
        Type[] args = Type.getArgumentTypes(method.desc);
        int slot = isStatic ? 0 : 1;
        int stackSlot = -1;
        for (Type arg : args) {
            if ("net/minecraft/item/ItemStack".equals(arg.getInternalName()) && stackSlot < 0) {
                stackSlot = slot;
            }
            slot += arg.getSize();
        }
        if (stackSlot < 0) {
            return;
        }

        InsnList hook = new InsnList();
        hook.add(new VarInsnNode(Opcodes.ALOAD, stackSlot));
        hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK_OWNER, HOOK_STACK, HOOK_STACK_DESC, false));
        method.instructions.insert(hook);
    }
}
