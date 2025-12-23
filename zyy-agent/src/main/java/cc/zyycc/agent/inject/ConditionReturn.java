package cc.zyycc.agent.inject;

import jdk.internal.org.objectweb.asm.Opcodes;

public enum ReturnType {//满足条件返回
    VOID(Opcodes.RETURN),
    BOOLEAN_TRUE(Opcodes.ICONST_1),
    BOOLEAN_FALSE(Opcodes.ICONST_0),
    CHAR(Opcodes.ICONST_0),
    BYTE(Opcodes.ICONST_0),
    SHORT(Opcodes.ICONST_0),
    INT(Opcodes.ICONST_0),
    LONG(Opcodes.LCONST_0),
    FLOAT(Opcodes.FCONST_0),
    DOUBLE(Opcodes.DCONST_0),
    OBJECT(Opcodes.ACONST_NULL),
    ARRAY(Opcodes.ACONST_NULL),
    STRING(Opcodes.ACONST_NULL),
    UNKNOWN(Opcodes.ACONST_NULL);


    private final int iconst;

    ReturnType(int iconst) {
        this.iconst = iconst;
    }

    public int getIconst() {
        return iconst;
    }

}