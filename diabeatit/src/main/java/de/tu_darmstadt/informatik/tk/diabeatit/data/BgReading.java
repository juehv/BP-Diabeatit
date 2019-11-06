package de.tu_darmstadt.informatik.tk.diabeatit.data;

import androidx.annotation.NonNull;

import java.time.format.DateTimeFormatter;
import java.util.Date;

public class BgReading {

    public enum Unit {
        MGDL,
        MMOLL;

        @Override
        public String toString() {
            switch(this) {
                case MGDL:
                    return "mg/dl";
                case MMOLL:
                    return "Mmol/L";
                default:
                    return "UNK";
            }
        }
    }

    public enum Source {
        MANUAL,
        EXTERNAL,
        SENSOR;

        @Override
        public String toString() {
            switch (this) {
                case MANUAL:
                    return "[M]";
                case EXTERNAL:
                    return "[E]";
                case SENSOR:
                    return "[S]";
                default:
                    return "[U]";
            }
        }
    }

    public BgReading() {
        // these _seem_ to be the defaults
        rawUnit = Unit.MGDL;
        source = Source.SENSOR;
    }

    public long timestamp;
    public double rawValue;
    public Unit rawUnit;
    public Source source;
    public String direction; // ??

    @NonNull
    @Override
    public String toString() {
        Date d = new Date(this.timestamp);

        return String.format("%s %s %s %s", d, this.source, this.rawValue, this.rawUnit);
    }
}

