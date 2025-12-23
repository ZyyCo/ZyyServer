package cc.zyycc.agent.inject.scan;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScanBase implements IScanVar {

    protected final Map<String, String> collectedFields = new ConcurrentHashMap<>();
    private final String[] variableCapture;

    public ScanBase(String... variableCapture) {
        this.variableCapture = variableCapture;
    }
    @Override
    public void scanField(String name, String descriptor) {
        for (String passParameter : variableCapture) {
            if (name.equals(passParameter)) {
                collectedFields.put(name, descriptor);
            }
        }
    }
}
