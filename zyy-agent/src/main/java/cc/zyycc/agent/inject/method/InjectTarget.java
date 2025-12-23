package cc.zyycc.agent.inject.method;

public interface InjectTarget {
    int getPoint();

    boolean matches(InsnContext ctx);

    int index();
}
