package cc.zyycc.agent.util;

public class FieldSignatureHandle extends FieldHandle {


    public FieldSignatureHandle(String originalSignature, String newSignature) {
        this(null, null, originalSignature, newSignature);
    }


    public FieldSignatureHandle(String name, String originalSignature, String newSignature) {
        this(null, name, originalSignature, newSignature);
    }

    public FieldSignatureHandle(String owner, String originalName, String originalSignature, String newSignature) {
        super(owner, originalName, originalSignature, newSignature);
    }

    @Override
    public String modify() {
        if (getNewSignature() == null) {
            return getSignature();
        }
        return getNewSignature();
    }

    @Override
    public String modifyName() {
        return getOriginalName();
    }


}
