package cc.zyycc.agent.inject.hookResult;

import cc.zyycc.agent.inject.VariableCapture;
import cc.zyycc.agent.inject.method.InjectTarget;

public class InjectVisitMethodHookResultReplace extends InjectVisitMethodHookResult{
    public InjectVisitMethodHookResultReplace(String injectClassName, String injectMethodName, VariableCapture varCapture, InjectTarget injectTarget) {
        super(injectClassName, injectMethodName, varCapture, injectTarget);
    }
}
