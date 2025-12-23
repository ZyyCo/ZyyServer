package cc.zyycc.agent.enhancer;

import cc.zyycc.agent.inject.InjectInfo;
import cc.zyycc.agent.inject.InjectVisitMethod;
import cc.zyycc.agent.inject.IInjectMode;
import cc.zyycc.agent.inject.hookResult.InjectInNewFunction;
import cc.zyycc.agent.inject.hookResult.InjectInNewFunctionBase;
import cc.zyycc.agent.inject.method.InjectMethod;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import cc.zyycc.agent.inject.visitCode.InjectMethodVisitor;
import cc.zyycc.agent.transformer.TransformerProvider;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


public class ConstructorEnhancer extends BaseEnhancer<InjectInNewFunctionBase> {
    private final String constructorDescriptor;
    private final String inheritConstructorDescriptor;
    private final int inheritanceMode;
    private String currentClass;
    private String superClass;
    private InjectInNewFunctionBase injectInNewFunction;


    public ConstructorEnhancer(String constructorDescriptor, String inheritConstructorDescriptor, int inheritanceMode) {
        this.constructorDescriptor = constructorDescriptor;
        this.inheritConstructorDescriptor = inheritConstructorDescriptor;
        this.inheritanceMode = inheritanceMode;
    }


    @Override
    public ClassVisitor createVisitor(ClassWriter cw, TransformerProvider transformerProvider, String pluginName, String className, ClassLoader classLoader) {

        return new ClassVisitor(Opcodes.ASM9, cw) {

            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                ConstructorEnhancer.this.currentClass = name;
                ConstructorEnhancer.this.superClass = superName;
                super.visit(version, access, name, signature, superName, interfaces);
            }

            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                injectInNewFunction.scanField(name, descriptor);
//                if (name.startsWith("field_")) {
//                    allInjectVisitMethods.put(name, descriptor);
//                }
                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public void visitEnd() {

                MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", constructorDescriptor,
                        null, null);
                mv.visitCode();
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                if (inheritConstructorDescriptor != null) {
                    injectSlots(Type.getArgumentTypes(inheritConstructorDescriptor), mv, currentClass, true);
                }
                if (inheritanceMode == 0) {
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClass, "<init>", "()V", false);      // 调用 super()
                } else {
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, currentClass, "<init>", inheritConstructorDescriptor, false);
                }
                InjectInfo injectInfo = injectSlots(Type.getArgumentTypes(constructorDescriptor), mv, currentClass, false);

                injectInfo = injectInNewFunction.injectCode(mv, injectInfo);
                //return
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(injectInfo.getMaxStack(), injectInfo.getMaxLocals());
                mv.visitEnd();
                super.visitEnd();
            }
        };
    }

    private InjectInfo injectSlots(Type[] types, MethodVisitor mv, String className, boolean push) {
        List<Integer> slotList = new ArrayList<>();
        Map<String, Integer> slots = new ConcurrentHashMap<>();
        slots.put("this", 0);
        int slot = 1;
        for (Type type : types) {
            slots.put(type.getDescriptor(), slot);
            slotList.add(slot);
            if (push) {
                mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), slot);
                injectInNewFunction.injectPush(mv, type.getClassName(), type.getDescriptor(), slot);
            }
            slot += type.getSize();  // long/double 占 2 个 slot
        }

        return new InjectInfo(Math.max(slot, 4), Math.max(slot, 1), className, superClass, slots, slotList);
    }

    @Override
    public boolean enhancer() {
        return true;
    }

    @Override
    public void addOrMerge(TargetMethod targetMethod, InjectInNewFunctionBase injectInNewFunction) {
        this.injectInNewFunction = injectInNewFunction;

    }

    @Override
    public boolean needFrame() {
        return false;
    }

    //    @Override
//    public ClassEnhancer getClassEnhancer() {
//        return this;
//    }
}

