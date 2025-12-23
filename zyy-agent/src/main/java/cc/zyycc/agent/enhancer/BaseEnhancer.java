package cc.zyycc.agent.enhancer;

import cc.zyycc.agent.inject.IInjectMode;


public abstract class BaseEnhancer<C extends IInjectMode> implements ClassEnhancer<C> {

    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean equals(Object o) {
        return o != null && getClass() == o.getClass();
    }

    public Object identityKey() {
        return getClass();
    }
}
