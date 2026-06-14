package foxiwhitee.FoxLib.asm.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.List;

@SuppressWarnings("unused")
public class GuiScreenTransformer implements IClassTransformer {
    private static final String TARGET_CLASS = "net.minecraft.client.gui.GuiScreen";

    private static final String HOOK_OWNER = "foxiwhitee/FoxLib/client/tooltips/TooltipEngine";
    private static final String HOOK_METHOD_COMPLEX = "renderVanillaTooltip";
    private static final String HOOK_DESC_COMPLEX = "(Ljava/lang/Object;Ljava/util/List;IILjava/lang/Object;)Z";
    private static final String HOOK_METHOD_SIMPLE = "renderVanillaTooltipSimply";
    private static final String HOOK_DESC_SIMPLE = "(Ljava/lang/Object;Ljava/util/List;II)Z";
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
            if (isRenderTooltipMethod(method)) {
                injectStackCaptureHook(method);
                changed = true;
            } else if (isComplexTooltipMethod(method)) {
                injectComplexHook(method);
                changed = true;
            } else if (isSimpleTooltipMethod(method)) {
                injectSimpleHook(method);
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

    private static boolean isComplexTooltipMethod(MethodNode method) {
        if (!"drawHoveringText".equals(method.name) && !"func_146283_a".equals(method.name)) {
            return false;
        }

        Type[] args = Type.getArgumentTypes(method.desc);
        return args.length == 4
            && Type.getType(List.class).equals(args[0])
            && Type.INT_TYPE.equals(args[1])
            && Type.INT_TYPE.equals(args[2])
            && args[3].getSort() == Type.OBJECT
            && Type.VOID_TYPE.equals(Type.getReturnType(method.desc));
    }

    private static boolean isRenderTooltipMethod(MethodNode method) {
        if (!"renderToolTip".equals(method.name) && !"func_146285_a".equals(method.name)) {
            return false;
        }

        Type[] args = Type.getArgumentTypes(method.desc);
        return args.length == 3
            && args[0].getSort() == Type.OBJECT
            && Type.INT_TYPE.equals(args[1])
            && Type.INT_TYPE.equals(args[2])
            && Type.VOID_TYPE.equals(Type.getReturnType(method.desc));
    }

    private static boolean isSimpleTooltipMethod(MethodNode method) {
        return ("drawHoveringText".equals(method.name) || "func_146283_a".equals(method.name))
            && "(Ljava/util/List;II)V".equals(method.desc);
    }

    private static void injectComplexHook(MethodNode method) {
        InsnList hook = new InsnList();
        LabelNode continueLabel = new LabelNode();

        hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
        hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
        hook.add(new VarInsnNode(Opcodes.ILOAD, 2));
        hook.add(new VarInsnNode(Opcodes.ILOAD, 3));
        hook.add(new VarInsnNode(Opcodes.ALOAD, 4));
        hook.add(new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            HOOK_OWNER,
            HOOK_METHOD_COMPLEX,
            HOOK_DESC_COMPLEX,
            false));
        hook.add(new JumpInsnNode(Opcodes.IFEQ, continueLabel));
        hook.add(new InsnNode(Opcodes.RETURN));
        hook.add(continueLabel);

        if (method.instructions.size() > 0) {
            method.instructions.insertBefore(method.instructions.getFirst(), hook);
        } else {
            method.instructions.insert(hook);
        }
    }

    private static void injectSimpleHook(MethodNode method) {
        InsnList hook = new InsnList();
        LabelNode continueLabel = new LabelNode();

        hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
        hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
        hook.add(new VarInsnNode(Opcodes.ILOAD, 2));
        hook.add(new VarInsnNode(Opcodes.ILOAD, 3));
        hook.add(new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            HOOK_OWNER,
            HOOK_METHOD_SIMPLE,
            HOOK_DESC_SIMPLE,
            false));
        hook.add(new JumpInsnNode(Opcodes.IFEQ, continueLabel));
        hook.add(new InsnNode(Opcodes.RETURN));
        hook.add(continueLabel);

        if (method.instructions.size() > 0) {
            method.instructions.insertBefore(method.instructions.getFirst(), hook);
        } else {
            method.instructions.insert(hook);
        }
    }

    private static void injectStackCaptureHook(MethodNode method) {
        InsnList hook = new InsnList();
        hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
        hook.add(new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            HOOK_OWNER,
            HOOK_METHOD_STACK,
            HOOK_DESC_STACK,
            false));
        if (method.instructions.size() > 0) {
            method.instructions.insertBefore(method.instructions.getFirst(), hook);
        } else {
            method.instructions.insert(hook);
        }
    }
}
