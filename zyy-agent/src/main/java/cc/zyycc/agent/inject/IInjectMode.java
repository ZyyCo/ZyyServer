package cc.zyycc.agent.inject;


public abstract class IInjectMode {

    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return true;
    }

    public abstract boolean needFrame();

}

