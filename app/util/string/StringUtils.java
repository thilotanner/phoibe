package util.string;

import java.util.Arrays;
import java.util.List;

public class StringUtils {
    public static String nonEmptyJoin(String delimiter,
                                      String... strings) {
        return nonEmptyJoin(delimiter, Arrays.asList(strings));
    }

    public static String nonEmptyJoin(String delimiter,
                                      List<String> strings) {
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
}
