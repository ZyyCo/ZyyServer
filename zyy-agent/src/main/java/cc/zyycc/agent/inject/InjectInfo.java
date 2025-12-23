package cc.zyycc.agent.inject;

import java.util.List;
import java.util.Map;

public class InjectInfo {
    private final List<Integer> slotList;
    public int maxStack;
    public int maxLocals;
    private final Map<String, Integer> slots;
    private final String className;
    private final String superClass;

    public InjectInfo(int maxStack, int maxLocals, String className, String superClass, Map<String, Integer> slots, List<Integer> slotList) {
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
        this.className = className;
        this.superClass = superClass;
        this.slots = slots;
        this.slotList = slotList;
    }

    public int getMaxStack() {
        return maxStack;
    }

    public int getMaxLocals() {
        return maxLocals;
    }

    public Map<String, Integer> getSlots() {
        return slots;
    }

    public List<Integer> getSlotList() {
        return slotList;
    }

    public String getClassName() {
        return className;
    }

    public String getSuperClass() {
        return superClass;
    }

    public void setMaxInfo(int maxStack, int maxLocals) {
        this.maxStack = Math.max(this.maxStack, maxStack);
        this.maxLocals = Math.max(this.maxLocals, maxLocals);
    }
}
