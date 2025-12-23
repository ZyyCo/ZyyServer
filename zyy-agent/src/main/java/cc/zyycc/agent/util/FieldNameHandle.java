package cc.zyycc.agent.util;

public class FieldNameHandle extends FieldHandle {

    private final String newName;

    public FieldNameHandle(String owner, String originalName, String newName) {
        super(owner, originalName, null);
        this.newName = newName;
    }

    public boolean match(String owner, String name, String desc) {
        if (owner.equals(this.owner) && name.equals(originalName)) {
            return true;
        }
        return false;
    }

    @Override
    public String modifyOwner() {
        return getOwner();
    }

    public String modifyDesc() {
        return getDesc();
    }

    @Override
    public String modifyName() {
        return newName;
    }
}
