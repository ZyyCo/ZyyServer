package cc.zyycc.agent.inject.hookResult;

import cc.zyycc.agent.inject.GlobalVarCapture;
import cc.zyycc.agent.inject.InjectInfo;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import cc.zyycc.agent.inject.method.InjectMethod;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;

public class InjectInNewFunction extends InjectInNewFunctionBase {


    public InjectInNewFunction(String injectPackageName, String injectClassName, String injectMethodName, GlobalVarCapture globalVarCapture, String returnName, String returnDesc) {
        super(injectPackageName, injectClassName, injectMethodName, globalVarCapture, new InjectReturnType(returnName, returnDesc));
    }

    public InjectInNewFunction(String injectPackageName, String injectClassName, String injectMethodName, GlobalVarCapture globalVarCapture, InjectReturnType returnType) {
        super(injectPackageName, injectClassName, injectMethodName, globalVarCapture, returnType);
    }

    public InjectInfo injectCode(MethodVisitor mv, InjectInfo injectInfo) {
        //压栈
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        StringBuilder sb = new StringBuilder("(");
        sb.append("L").append(injectInfo.getClassName()).append(";"); // 第一个参数 this

        globalVarCapture.load(mv, injectInfo.getClassName());
        sb.append(globalVarCapture.getDesc());

        if (getReturnType() == null) {
            sb.append(")V");
        } else {
            sb.append(")").append(getReturnType().getDesc());
        }
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                getInjectClassName(),
                getInjectMethodName(), sb.toString(), false);
        int go = 1;
        if (getReturnType() != null) {
            go = getReturnType().go(injectInfo.getClassName(), mv, 0);
        }
        injectInfo.setMaxInfo(go, 1);
        return injectInfo;
    }

    @Override
    public boolean needFrame() {
        return false;
    }
}
