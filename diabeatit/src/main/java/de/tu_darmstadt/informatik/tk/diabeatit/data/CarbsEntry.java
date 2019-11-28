package de.tu_darmstadt.informatik.tk.diabeatit.data;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.util.Date;

public class CarbsEntry {
    public long timestamp;
    public double carbs;

    @NonNull
    @Override
    public String toString() {
        String ts = Instant.ofEpochMilli(timestamp).toString();

        return String.format("CarbsEntry { timestamp=%s carbs=%f }",
                ts, carbs);
    }
}
