package cc.zyycc.remap;

import java.lang.reflect.Field;

public class BaseEntry {
    private final String name2;
    protected String className;

    public static BaseEntry empty() {
        return new BaseEntry(null, null);
    }


    public boolean isEmpty() {
        return className == null;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String generate() {
        if (name2 == null) {
            return className;
        }
        return className + " " + name2;
    }

    public BaseEntry(String className) {
        this.className = className.replace(".", "/");
        this.name2 = null;
    }

    public BaseEntry(String className, String name2) {
        this.className = className.replace(".", "/");
        this.name2 = name2;
    }

    public String getName2() {
        return name2;
    }

    public BaseEntry recreate(String value) {
        int s1 = value.indexOf(" ");
        int s2 = value.indexOf(" ", s1);
        if (s1 == -1) {
            throw new RuntimeException(value);
        }
        String left = value.substring(0, s1);
        String right;
        if (s2 == -1) {
            right = "";
        } else {
            right = value.substring(s2 + 1);
        }
        return new BaseEntry(left, right);
    }

    public Field anewExecuteField(ClassLoader loader) {

        try {
            Class<?> target = Class.forName(className.replace("/", "."), false, loader);
            // ① 先 declaredField（声明类最快、最准确）
            try {
                return target.getDeclaredField(name2);
            } catch (NoSuchFieldException ignore) {
            }


            return target.getField(name2);

        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }


}
