package cc.zyycc.common.mapper.cache;

import cc.zyycc.common.mapper.MappingEntry;

public class MappingCacheTable {
    public static final int PATTERN_EMPTY = 0;
    public static final int PATTERN_SUCCESS = 1;
    public static final int PATTERN_FAIL = 2;
    public final int pattern;
    private MappingEntry entry;

    private MappingCacheTable(int pattern, MappingEntry entry) {
        this.pattern = pattern;
        this.entry = entry;
    }
    public static MappingCacheTable empty() {
        return new MappingCacheTable(PATTERN_EMPTY, null);
    }

    public static MappingCacheTable createFailCacheTable(MappingEntry entry) {
//        System.err.println("[MappingCacheTable] Fail to cache " + entry.getClassName());
        return new MappingCacheTable(PATTERN_FAIL, entry);
    }

    public static MappingCacheTable createSuccessCacheTable(MappingEntry valueEntry) {
        return new MappingCacheTable(PATTERN_SUCCESS, valueEntry);
    }

    public MappingEntry getEntry() {
        return entry;
    }
}
