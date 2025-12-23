package cc.zyycc.remap;

import java.util.function.Consumer;

public class MappingHandle {
    private final BaseEntry leftEntry;
    private final BaseEntry rightEntry;
    public MappingHandle(BaseEntry leftEntry, BaseEntry rightEntry) {
        this.leftEntry = leftEntry;
        this.rightEntry = rightEntry;
    }



    public static MappingHandle empty() {
        return new MappingHandle(null, null);
    }

    public boolean isEmpty() {
        return leftEntry == null && rightEntry == null;
    }

    public void handle(Consumer<MappingHandle> consumer) {
        consumer.accept(this);
    }

    public BaseEntry getLeftEntry() {
        return leftEntry;
    }

    public BaseEntry getRightEntry() {
        return rightEntry;
    }
}
