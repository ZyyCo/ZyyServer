package cc.zyycc.remap.cache;

public class DefaultRule implements CacheRule{
    @Override
    public BaseCache.Rule lineRule(String line) {
        int s1 = line.indexOf(' ');            // between token0 and token1
        int s2 = line.indexOf(' ', s1 + 1);    // between token1 and token2
        int s3 = line.indexOf(' ', s2 + 1);    // between token2 and token3


        // Fail case (no space)
        if (s1 == -1) {
            return new BaseCache.Rule(line, "");
        }

        // Fail case (only 2 tokens: oldOwner oldDesc)
        if (s2 == -1) {
            String left = line.substring(0, s1);
            String right =  line.substring(s1 + 1);
            return new BaseCache.Rule(left, right);
        }

        // Only 3 tokens (rare)
        if (s3 == -1) {
            String left = line.substring(0, s1) + " " + line.substring(s1 + 1, s2);
            String right = line.substring(s2 + 1);
            return new BaseCache.Rule(left, right);
        }

        // Normal 4-token mapping line
        String left = line.substring(0, s1) + " " + line.substring(s1 + 1, s2);
        String right = line.substring(s2 + 1, s3) + " " + line.substring(s3 + 1);

        return new BaseCache.Rule(left, right);
    }
}
