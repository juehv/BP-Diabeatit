package de.tu_darmstadt.informatik.tk.diabeatit.data;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import de.tu_darmstadt.informatik.tk.diabeatit.data.BolusSources.ManualBolusSource;

public class SportsEntry {
    public enum Level {
        LOW, MEDIUM, HIGH;

        public int toInt() {
            switch (this) {
                case LOW:
                    return 0;
                case MEDIUM:
                    return 1;
                case HIGH:
                    return 2;
                default:
                    return -1;
            }
        }
        public static Level fromInt(int i) {
            switch (i) {
                case 0:
                    return LOW;
                case 1:
                    return MEDIUM;
                case 2:
                    return HIGH;
                default:
                    return null;
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case LOW: return "LOW";
                case MEDIUM: return "MEDIUM";
                case HIGH: return "HIGH";
                default: return "UNKNOWN";
            }
        }
    }

    public long timestampFrom;
    public long timestampUntil;
    public Level level;
    public String notes;

    @NonNull
    @Override
    public String toString() {
        Instant from = Instant.ofEpochMilli(timestampFrom);
        String tsFrom = from.toString();
        Instant until = Instant.ofEpochMilli(timestampUntil);
        String tsUntil = until.toString();
        return String.format("SportsEntry { from='%s' until='%s' level=%s notes='%s' }",
                tsFrom, tsUntil, level.toString(), notes);
    }
}
