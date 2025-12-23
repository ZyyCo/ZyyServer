package cc.zyycc.common.bridge;

import java.lang.instrument.Instrumentation;

public class InstrumentationBridge {
    public static Instrumentation instrumentation;

    public static Instrumentation getInst() {
        return instrumentation;
    }
}
