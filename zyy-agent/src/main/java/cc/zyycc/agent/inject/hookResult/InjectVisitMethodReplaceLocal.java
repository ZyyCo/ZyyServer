package cc.zyycc.agent.inject.hookResult;

import cc.zyycc.agent.VarCapture;
import cc.zyycc.agent.inject.ConditionReturn;
import cc.zyycc.agent.inject.IReturnCapture;
import cc.zyycc.agent.inject.VariableCapture;
import cc.zyycc.agent.inject.method.InjectTarget;
import cc.zyycc.agent.inject.returnType.ReturnType;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import cc.zyycc.agent.inject.scan.IScanVar;
import cc.zyycc.agent.transformer.scan.IScan;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InjectVisitMethodReplaceLocal extends InjectVisitMethodHookBase {

    private final IReturnCapture returnCapture;
    private IScanVar scan;
    private ConditionReturn conditionReturn;

    //String
    public InjectVisitMethodReplaceLocal(String returnPackage, String injectClassName, String injectMethodName, VariableCapture varCapture, InjectTarget injectTarget, IReturnCapture returnCapture, IScanVar scan) {
        super(returnPackage, injectClassName, injectMethodName, varCapture, injectTarget);
        this.returnCapture = returnCapture;
        this.scan = scan;
    }

    //int
    public InjectVisitMethodReplaceLocal(String returnPackage, String injectClassName, String injectMethodName, VariableCapture varCapture, InjectTarget injectTarget, IReturnCapture returnCapture, ConditionReturn conditionReturn) {
        super(returnPackage, injectClassName, injectMethodName, varCapture, injectTarget);
        this.returnCapture = returnCapture;
        this.conditionReturn = conditionReturn;
    }

    @Override
    public int injectResult(MethodVisitor mv, int resultIndex, String currentClassName) {
        returnCapture.pushReplaceVar(mv, resultIndex, returnType.getDesc());
        return resultIndex;
    }

    @Override
    public InjectReturnType getReturnType(MethodVisitor mv, String currentClassName) {
        if (conditionReturn != null) {
            return this.returnType = new ReturnType(conditionReturn.getDesc());
        } else {
            return this.returnType = new ReturnType(returnCapture.getDesc());
        }
    }

    @Override
    public void scanField(String name, String descriptor) {
        if (scan != null) {
            scan.scanField(name, descriptor);
        }
        super.scanField(name, descriptor);
    }


    @Override
    public boolean needFrame() {
        return false;
    }


}
