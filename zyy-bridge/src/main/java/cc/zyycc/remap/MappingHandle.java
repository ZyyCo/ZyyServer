package cc.zyycc.common.mapper;

import java.util.function.Consumer;

public class MappingHandle {
    private final MappingEntry leftEntry;
    private final MappingEntry rightEntry;
    public MappingHandle(MappingEntry leftEntry, MappingEntry rightEntry) {
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

    public MappingEntry getLeftEntry() {
        return leftEntry;
    }

    public MappingEntry getRightEntry() {
        return rightEntry;
    }
}
