package cc.zyycc.common.cache;

public class CacheMapperData {

    private final String className;
    private final String name2;
    private String desc;
    private boolean errorCache = false;

    public CacheMapperData(String className, String name2) {
        this.className = className;
        this.name2 = name2;

    }

    public CacheMapperData(String className, String name2, String desc) {
        this.className = className;
        this.name2 = name2;
        this.desc = desc;
    }

    public String getClassName() {
        return className;
    }

    public String getName2() {
        return name2;
    }

    public String getDesc() {
        return desc;
    }
    public static CacheMapperData error(String className, String name2) {
        CacheMapperData cacheMapperData = new CacheMapperData(null, null, null);
        cacheMapperData.errorCache = true;
        return cacheMapperData;
    }

    public static CacheMapperData error(String className, String name2, String desc) {
        CacheMapperData cacheMapperData = new CacheMapperData(null, null, null);
        cacheMapperData.errorCache = true;
        return cacheMapperData;
    }

    public boolean isError() {
        return errorCache;
    }
}
