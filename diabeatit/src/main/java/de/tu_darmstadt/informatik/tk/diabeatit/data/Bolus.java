package de.tu_darmstadt.informatik.tk.diabeatit.data;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.time.Instant;

public class Bolus {
    public long timestamp;
    public double units;
    public String notes;

    @NonNull
    @Override
    public String toString() {
        Instant i = Instant.ofEpochMilli(timestamp);
        String ts = i.toString();

        return String.format("Bolus { timestamp=%s units=%f notes='%s' }", ts, units, notes);
    }
}
