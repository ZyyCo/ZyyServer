package cc.zyycc.agent.inject.method;

public class InjectFieldTarget implements InjectTarget {
    private final int point;
    private final String owner;
    private final String name;
    private final String descriptor;
    private final int index;
    private int aIndex;

    public InjectFieldTarget(int point) {
        this(point, null, null, null);
    }

    public InjectFieldTarget(String name) {
        this(InjectPoint.INVOKE_BEFORE, null, name, null);
    }

    public InjectFieldTarget(String name, String descriptor) {
        this.point = InjectPoint.INVOKE_BEFORE;
        this.owner = null;
        this.name = name;
        this.descriptor = descriptor;
        this.index = 1;
    }

    public InjectFieldTarget(String name, String descriptor, int index) {
        this.point = InjectPoint.INVOKE_BEFORE;
        this.owner = null;
        this.name = name;
        this.descriptor = descriptor;
        this.index = index;
    }

    public InjectFieldTarget(int point, String owner, String name, String descriptor) {
        this.point = point;
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.index = 1;
    }


    public static InjectFieldTarget point(int injectPoint) {
        return new InjectFieldTarget(injectPoint);
    }

    @Override
    public int getPoint() {
        return point;
    }

    @Override
    public boolean matches(InsnContext ctx) {
        if (aIndex != index) {
            boolean nameDescMatch = name.equals(ctx.name()) && descriptor.equals(ctx.desc());
            boolean ownerMatch = (owner == null) || owner.equals(ctx.owner());

            if (nameDescMatch && ownerMatch) {
                aIndex++;
                if (aIndex == index) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public int index() {
        return index;
    }


}
