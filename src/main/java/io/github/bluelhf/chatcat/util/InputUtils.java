package io.github.bluelhf.chatcat.util;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.format.DateTimeParseException;

public class InputUtils {

    /**
     * Parses a given ISO-8601 duration string into a duration object<br/>
     * Format: PnDTnHnMn.nS, see <a href="https://en.m.wikipedia.org/wiki/ISO_8601#Durations">the ISO-8601 wikipedia page.</a><br/>
     * <br/>
     * Automatically adds P and T characters correctly, provided they are missing.<br/>
     * Also supports 'forever' and 'eternity' keywords in case you want an infinite duration.<br/>
     * Infinite durations are of length -1 seconds<br/>
     *
     * @param input The string to format.
     * @return      The parsed duration, or null if parsing failed.
     * @see <a href="https://en.wikipedia.org/wiki/ISO_8601#Durations">ISO-8601 Wikipedia Page
     */
    @Nullable public static Duration inputToDuration(String input) {
        if (input.equalsIgnoreCase("forever") ||
            input.equalsIgnoreCase("eternity")) return Duration.ofSeconds(-1);


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
}

