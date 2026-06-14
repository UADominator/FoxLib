package foxiwhitee.FoxLib.asm.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

@SuppressWarnings("unused")
public class GuiContainerTransformer implements IClassTransformer {
    private static final String TARGET_CLASS = "net.minecraft.client.gui.inventory.GuiContainer";

    private static final String HOOK_OWNER = "foxiwhitee/FoxLib/client/tooltips/TooltipEngine";
    private static final String HOOK_METHOD_STACK = "setHoveredStack";
    private static final String HOOK_DESC_STACK = "(Ljava/lang/Object;)V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return null;
        }
        if (!TARGET_CLASS.equals(transformedName)) {
            return basicClass;
        }

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        boolean changed = false;
        for (MethodNode method : classNode.methods) {
            if (isItemStackTooltipMethod(method)) {
                injectStackCapture(method);
                changed = true;
            }
        }

        if (!changed) {
            return basicClass;
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static boolean isItemStackTooltipMethod(MethodNode method) {
        boolean nameMatches = "drawItemStackTooltip".equals(method.name)
            || "func_146285_a".equals(method.name)
            || "func_146982_a".equals(method.name);
        if (!nameMatches) {
            return false;
        }
        Type[] args = Type.getArgumentTypes(method.desc);
        if (args.length < 3 || args.length > 5) {
            return false;
        }
        if (!"net/minecraft/item/ItemStack".equals(args[0].getInternalName())) {
            return false;
        }
        if (!Type.INT_TYPE.equals(args[1]) || !Type.INT_TYPE.equals(args[2])) {
            return false;
        }
        return Type.VOID_TYPE.equals(Type.getReturnType(method.desc));
    }

    private static void injectStackCapture(MethodNode method) {
        InsnList hook = new InsnList();
        hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
        hook.add(new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            HOOK_OWNER,
            HOOK_METHOD_STACK,
            HOOK_DESC_STACK,
            false));
        method.instructions.insert(hook);
    }
}
