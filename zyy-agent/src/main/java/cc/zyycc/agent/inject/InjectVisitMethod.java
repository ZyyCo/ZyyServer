package cc.zyycc.agent.enhancer;

import cc.zyycc.agent.inject.method.*;

import java.util.function.Predicate;

public class InjectVisitMethod extends InjectMethod {

    public static final int AFTER_SUPER = 0;
    public static final int AFTER_RETURN = 1;
    public final String[] injectFields;

    private final InjectTarget injectTarget;


    protected Predicate<String> desc = desc -> false;

    public int injectPosition = -1;

    private final InsnContext insnContext = new InsnContext();

    public InjectVisitMethod(String injectClassName, String injectMethodName, InjectTarget injectTarget, String... injectFields) {
        super(injectClassName, injectMethodName);
        this.injectTarget = injectTarget;
        this.injectFields = injectFields;
    }


    public InjectVisitMethod(String injectClassName, String injectMethodName, InjectTarget injectTarget, InjectReturnType returnType, String... injectFields) {
        super(injectClassName, injectMethodName, returnType);
        this.injectFields = injectFields;
        this.injectTarget = injectTarget;
    }



    public boolean isTargetMethod(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(injectTarget instanceof InjectMethodTarget){
            return injectTarget.matches(insnContext.setMethod(opcode, owner, name, descriptor, isInterface));
        }
        return false;
    }

    public boolean isTargetField(int opcode, String owner, String name, String descriptor) {
        if(injectTarget instanceof InjectFieldTarget){
            return injectTarget.matches(insnContext.setField(opcode, owner, name, descriptor));
        }
        return false;
    }

    public InjectTarget getInjectTarget() {
        return injectTarget;
    }
}
