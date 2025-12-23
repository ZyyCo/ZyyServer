package cc.zyycc.remap.cache;

import cc.zyycc.remap.BaseEntry;

public class MappingCacheTable {
    public static final int PATTERN_EMPTY = 0;
    public static final int PATTERN_SUCCESS = 1;
    public static final int PATTERN_FAIL = 2;
    public final int pattern;
    private final BaseEntry entry;

    private MappingCacheTable(int pattern, BaseEntry entry) {
        this.pattern = pattern;
        this.entry = entry;
    }

    public static MappingCacheTable empty() {
        return new MappingCacheTable(PATTERN_EMPTY, null);
    }

    public static MappingCacheTable createFailCacheTable(BaseEntry entry) {
//        System.err.println("[MappingCacheTable] Fail to cache " + entry.getClassName());
        return new MappingCacheTable(PATTERN_FAIL, entry);
    }

    public static MappingCacheTable createSuccessCacheTable(BaseEntry valueEntry) {
        return new MappingCacheTable(PATTERN_SUCCESS, valueEntry);
    }

    public BaseEntry getEntry() {
        return entry;
    }


    public void sendError(String error) {
        System.err.println("[SimpleRemapper] Fail to " + entry.getClassName() + " " + entry.getName2());
    }
}
