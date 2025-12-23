package cc.zyycc.agent.inject.method;

public class InjectVisitInsnTarget implements InjectTarget {


    private final int opcode;
    private final int index;
    private final int point;

    private int aIndex;

    public InjectVisitInsnTarget(int opcode, int point) {
        this.opcode = opcode;
        this.index = 1;
        this.point = point;
    }


    public InjectVisitInsnTarget(int opcode, int index, int point) {
        this.opcode = opcode;
        this.index = index;
        this.point = point;

    }

    @Override
    public int getPoint() {
        return point;
    }

    @Override
    public boolean matches(InsnContext ctx) {
        if (aIndex != index) {
            if (opcode == ctx.opcode()) {
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
        return 0;
    }
}
