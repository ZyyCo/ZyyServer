package cc.zyycc.agent.enhancer;

import cc.zyycc.agent.inject.IInjectMode;
import cc.zyycc.agent.inject.visitCode.InjectVisitCode;
import cc.zyycc.agent.transformer.TransformerProvider;

public abstract class FieldEnhancer<C extends InjectVisitCode> extends BaseEnhancer<C> {

    private TransformerProvider transformerProvider;
    private String targetMethodName;
    private InjectVisitCode iVisitCode;
    protected boolean enhancer = false;

    @Override
    public boolean enhancer() {
        return enhancer;
    }

    public FieldEnhancer() {
    }

    public FieldEnhancer(TransformerProvider transformerProvider, String targetMethodName, InjectVisitCode iVisitCode) {
        this.transformerProvider = transformerProvider;
        this.targetMethodName = targetMethodName;
        this.iVisitCode = iVisitCode;
    }
}
