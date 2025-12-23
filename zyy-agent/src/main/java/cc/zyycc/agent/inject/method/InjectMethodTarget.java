package cc.zyycc.agent.inject.method;

public class InjectMethodTarget implements InjectTarget{
    private final int point;
    private final String owner;
    private final String name;
    private final String desc;
    private final boolean isInterface;

    public InjectMethodTarget(int point, String owner, String name, String desc, boolean isInterface) {
        this.point = point;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.isInterface = isInterface;
    }


    @Override
    public int getPoint() {
        return point;
    }

    @Override
    public boolean matches(InsnContext ctx) {
        return ctx.owner() != null
                && ctx.owner().equals(owner)
                && ctx.name().equals(name)
                && ctx.desc().equals(desc);
    }

    @Override
    public int index() {
        return 0;
    }
}
