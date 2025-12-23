package cc.zyycc.agent.inject.hookResult;

import cc.zyycc.agent.inject.GlobalVarCapture;
import cc.zyycc.agent.inject.method.InjectMethod;

public class InjectNewFunction extends InjectMethod {
    public InjectNewFunction(String injectClassName, String injectMethodName, GlobalVarCapture globalVarCapture,String targetMethod, String targetMethodDesc) {
        super(injectClassName, injectMethodName);
    }
}
