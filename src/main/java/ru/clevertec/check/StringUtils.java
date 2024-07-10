package ru.clevertec.check;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public class StringUtils {

    private StringUtils() {}

    public static List<String> findStringsWithPrefix(List<String> strings, String prefix) {
        List<String> result = new ArrayList<>();

        strings.forEach(s -> {

            if (!s.startsWith(prefix)) {
                return;
            }

            result.add(s);
        });

        return result;
    }

    public static Optional<String> removePrefix(String string, String prefix) {

        int nonPrefixIndex = string.indexOf(prefix) + prefix.length();
        if (nonPrefixIndex == string.length()) {
            return Optional.empty();
        }

        return Optional.of(string.substring(nonPrefixIndex));
    }

    public static <T> Optional<T> mapStringIfMatchesRegex(String string, String regex, Function<String, T> mapper) {

        if (!Pattern.matches(regex, string)) {
            return Optional.empty();
        }

        return Optional.of(
                mapper.apply(string)
        );
    }
}
