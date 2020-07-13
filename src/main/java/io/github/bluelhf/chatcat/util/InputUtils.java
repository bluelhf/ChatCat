package io.github.bluelhf.chatcat.util;

import io.github.bluelhf.chat.Chat;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.format.DateTimeParseException;

public class InputUtils {

    /**
     * Parses a given ISO-8601 duration string into a duration object
     * Format: PnDTnHnMn.nS, see https://en.m.wikipedia.org/wiki/ISO_8601#Durations
     *
     * Automatically adds P and T characters correctly, provided they are missing.
     * Also supports 'forever' and 'eternity' keywords in case you want an infinite duration.
     * Infinite durations are of length -1 seconds
     *
     * @param input The string to format.
     * @return      The parsed duration, or null if parsing failed.
     */
    @Nullable public static Duration inputToDuration(String input) {
        if (input.equalsIgnoreCase("forever") ||
            input.equalsIgnoreCase("eternity")) return Duration.ofSeconds(-1);


        Chat.getInstance().log(input, Level.DEBUG, true, true);
        try {
            return Duration.parse(input);
        } catch (DateTimeParseException e1) {
            // If that didn't work, try with this instead.
            if (input.toLowerCase().contains("d")) {
                input = "P" + input;
                if (!input.toLowerCase().endsWith("d")) input = input.replaceAll("([dD])", "dT");
            } else {
                input = "PT" + input;
            }

            try { return Duration.parse(input); } catch (Exception e2) { return null; }

        }
    }

    /**
     * Parses a given String into its corresponding java.util.logging.Level field.
     * @param input The string to parse.
     * @return      The corresponding java.util.logging.Level field, or fallbackLevel if parsing failed.
     */
    public static @NotNull Level stringToLevel(@NotNull String input, @NotNull Level fallbackLevel) {
        return Level.toLevel(input, fallbackLevel);
    }
}

