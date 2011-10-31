package util.string;

import java.util.List;

public class StringUtils {
    public static String nonEmptyJoin(String[] strings,
                                      String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            if (string != null && !string.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(delimiter);
                }
                sb.append(string);
            }
        }
        return sb.toString();
    }

    public static String nonEmptyJoin(List<String> strings,
                                      String delimiter) {
        String[] string = new String[strings.size()];
        return nonEmptyJoin(strings.toArray(string), delimiter);
    }
}
