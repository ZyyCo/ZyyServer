package cc.zyycc.agent.util;

public abstract class FieldHandle {
    private final String desc;
    protected final String originalName;
    protected final String owner;

    public FieldHandle(String desc) {
        this(null, null, desc);
    }

    public FieldHandle(String owner, String originalName, String desc) {
        this.owner = owner;
        this.originalName = originalName;
        this.desc = desc;
    }

    public boolean match(String name, String desc) {
        if (originalName == null) {
            return this.desc.equals(desc);
        }
        return this.originalName.equals(name) && this.desc.equals(desc);
    }

    public boolean matchOwner(String owner) {
        if (originalName == null) {
            return this.desc.equals(owner);
        }
        return false;
    }


    public boolean matchDesc(String name, String desc) {
        if (originalName == null) {
            return this.desc.equals(desc);
        }
        return this.originalName.equals(name) && this.desc.equals(desc);
    }

    public boolean match(String owner, String name, String desc) {
        if (this.owner == null && originalName == null) {
            return this.desc.equals(desc);
        }
        if (originalName == null) {
            return this.desc.equals(desc) && this.owner.equals(owner);
        }
        if (this.owner == null) {
            return this.desc.equals(desc) && this.originalName.equals(name);
        }
        return this.originalName.equals(name) && this.desc.equals(desc) && this.owner.equals(owner);
    }


    public abstract String modifyOwner();

    public abstract String modifyDesc();

    public abstract String modifyName();

    public String getOriginalName() {
        return originalName;
    }


    public String getDesc() {
        return desc;
    }

    public String getOwner() {
        return owner;
    }
}
