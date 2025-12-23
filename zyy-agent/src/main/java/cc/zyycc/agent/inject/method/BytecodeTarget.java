package cc.zyycc.agent.inject.method;

import cc.zyycc.agent.inject.method.byteMatcher.BytecodeMatcher;

public class BytecodeTarget implements InjectTarget{

    private final int point;
    private final BytecodeMatcher matcher;

    public BytecodeTarget(int point, BytecodeMatcher matcher) {
        this.point = point;
        this.matcher = matcher;
    }
    @Override
    public int getPoint() {
        return point;
    }

    @Override
    public boolean matches(InsnContext ctx) {
        return false;
    }

    @Override
    public int index() {
        return 0;
    }
}
