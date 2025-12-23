package cc.zyycc.remap.cache;

public class ArrayRule implements CacheRule {


    @Override
    public BaseCache.Rule lineRule(String line) {

        int s1 = line.indexOf(' ');

        int arr = line.indexOf('[');
        int arr2 = line.indexOf(']');
        // Fail case (no space)
        if (arr == -1 || arr2 == -1) {
            return new BaseCache.Rule(line);
        }
        String array = line.substring(arr, arr2 + 1).trim();
        if (s1 == -1) {
            return new BaseCache.Rule(line.substring(0, arr), array);
        }


        String left = line.substring(0, s1);
        String right = array;

        return new BaseCache.Rule(left, right);
    }
}
