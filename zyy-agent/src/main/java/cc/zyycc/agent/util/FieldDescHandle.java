package cc.zyycc.agent.util;

public class FieldDescHandle extends FieldHandle {
    private final String newDesc;
    private final boolean reverseInterface;
    private final String twoStageOwner;

    public FieldDescHandle(String desc, String newDesc) {
        this(null, desc, newDesc, false);
    }

    public FieldDescHandle(String desc, String newDesc, boolean reverseInterface) {
        this(null, desc, newDesc, reverseInterface);
    }

    public FieldDescHandle(String name, String desc, String newDesc, boolean reverseInterface) {
        super(null, name, desc);
        this.newDesc = newDesc;
        this.reverseInterface = reverseInterface;
        this.twoStageOwner = desc.substring(desc.indexOf('L') + 1, desc.indexOf(';'));
    }


    public boolean isReverseInterface() {
        return reverseInterface;
    }


    @Override
    public String modifyOwner() {
        return getOwner();
    }

    @Override
    public String modifyDesc() {
        return newDesc;
    }

    @Override
    public String modifyName() {
        return getOriginalName();
    }


    public boolean twoStageMatch(String owner) {
        return owner.equals(twoStageOwner);
    }

    public String getNewOwner() {
        return newDesc.substring(newDesc.indexOf('L') + 1, newDesc.indexOf(';'));
    }
}
