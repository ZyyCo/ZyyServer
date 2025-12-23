package cc.zyycc.agent.inject;


import org.objectweb.asm.Opcodes;

public enum ConditionReturn {//满足条件返回
    VOID(Opcodes.RETURN, "V"),
    BOOLEAN_TRUE(Opcodes.ICONST_1, "Z"),
    BOOLEAN_FALSE(Opcodes.ICONST_0, "Z"),
    CHAR(Opcodes.ICONST_0, "C"),
    BYTE(Opcodes.ICONST_0, "B"),
    SHORT(Opcodes.ICONST_0, "S"),
    INT(Opcodes.ICONST_0, "I"),
    LONG(Opcodes.LCONST_0, "J"),
    FLOAT(Opcodes.FCONST_0, "F"),
    DOUBLE(Opcodes.DCONST_0, "D"),
    OBJECT(Opcodes.ACONST_NULL, "Ljava/lang/Object;"),
    ARRAY(Opcodes.ACONST_NULL, "[Ljava/lang/Object;"),
    STRING(Opcodes.ACONST_NULL, "Ljava/lang/String;"),
    UNKNOWN(Opcodes.ACONST_NULL, "Ljava/lang/Object;");


    private final int iconst;
    private final String desc;

    ConditionReturn(int iconst, String desc) {
        this.iconst = iconst;
        this.desc = desc;
    }

    public int getIconst() {
        return iconst;
    }

    public String getDesc() {
        return desc;
    }
}