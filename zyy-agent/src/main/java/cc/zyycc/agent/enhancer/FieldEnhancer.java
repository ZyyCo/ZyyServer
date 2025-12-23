package cc.zyycc.agent.enhancer;

import cc.zyycc.agent.transformer.ClassEnhancer;

public abstract class FieldPerEnhancer implements ClassEnhancer {

    protected boolean enhancer =  false;
    @Override
    public boolean enhancer() {
        return enhancer;
    }
}
