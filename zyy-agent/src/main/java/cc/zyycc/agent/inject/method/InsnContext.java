package cc.zyycc.agent.inject.method;

public class InsnContext {

    private int opcode;
    private String owner, name, desc;
    private boolean isInterface;

    public InsnContext() {

    }

    public InsnContext setField(int opcode, String owner, String name, String desc) {
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        return this;
    }

    public InsnContext setMethod(int opcode, String owner, String name, String desc, boolean isInterface) {
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.isInterface = isInterface;
        return this;
    }

    public InsnContext setCode(int opcode) {
        this.opcode = opcode;
        return this;
    }


    public int opcode() {
        return opcode;
    }

    public String owner() {
        return owner;
    }

    public String name() {
        return name;
    }

    public String desc() {
        return desc;
    }

    public boolean isInterface() {
        return isInterface;
    }


}

