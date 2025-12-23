package cc.zyycc.common.util.asm;

public final class HookResult {
    public final boolean cancelled;
    public final Object returnValue; // null 表示不替换 return
    public final Object replacementParseResults; // 可为具体类型，或 null
    public final Throwable exception; // 若非空可抛出


    private HookResult(boolean cancelled, Integer returnValue, Object replacementParseResults, Throwable exception) {
        this.cancelled = cancelled;
        this.returnValue = returnValue;
        this.replacementParseResults = replacementParseResults;
        this.exception = exception;
    }

    public static HookResult proceed() {
        return new HookResult(false, null, null, null);
    }

    public static HookResult cancelWith(int value) {
        return new HookResult(true, value, null, null);
    }

    public static HookResult replaceParseResults(Object newParse) {
        return new HookResult(false, null, newParse, null);
    }

    public static HookResult cancelAndReplace(int value, Object newParse) {
        return new HookResult(true, value, newParse, null);
    }

    public static HookResult throwException(Throwable t) {
        return new HookResult(true, null, null, t);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public Object getResult() {
        return returnValue;
    }


    public static HookResult notProcess() {
        return new HookResult(false, null, null, null);
    }


}
