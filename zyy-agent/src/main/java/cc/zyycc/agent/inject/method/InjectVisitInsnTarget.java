package cc.zyycc.agent.inject.method;

public class InjectVisitCodeTarget implements InjectTarget {


    private final int opcode;
    private final int index;

    private int aIndex;

    public InjectVisitCodeTarget(int opcode) {
        this.opcode = opcode;
        this.index = 0;
    }


    public InjectVisitCodeTarget(int opcode, int index) {
        this.opcode = opcode;
        this.index = index;
    }

    @Override
    public int getPoint() {
        return 0;
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
