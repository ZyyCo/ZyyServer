package cc.zyycc.plugin.util;

import java.util.Objects;

public class FiledEntry {
    public final String confusedFiled;
    public final String name;

    public FiledEntry(String name, String confusedFiled) {
        this.name = name;
        this.confusedFiled = confusedFiled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiledEntry that = (FiledEntry) o;
        return Objects.equals(confusedFiled, that.confusedFiled) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(confusedFiled, name);
    }
}
