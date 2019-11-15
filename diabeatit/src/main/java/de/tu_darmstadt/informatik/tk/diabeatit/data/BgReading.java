package de.tu_darmstadt.informatik.tk.diabeatit.data;

import androidx.annotation.NonNull;

import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Represents a single point of data reading the BG values
 */
public class BgReading {

    /**
     * Unit of a BgReading
     */
    public enum Unit {
        /** mg/dL */
        MGDL,
        /** Mmol/L */
        MMOLL;

        @Override
        public String toString() {
            switch(this) {
                case MGDL:
                    return "mg/dl";
                case MMOLL:
                    return "Mmol/L";
                default:
                    throw new UnsupportedOperationException("Reached unreachable code");
            }
        }
    }

    /**
     * The source a BgReading was received from
     */
    public enum Source {
        /** The reading was provided manually */
        MANUAL,
        /** The reading was provided by an external source that is not a sensor, e.g. NightScout */
        EXTERNAL,
        /** The reading was provided by a supported Sensor */
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

    /** Creates a new BgReading that is a copy of another */
    public BgReading(BgReading other) {
        timestamp = other.timestamp;
        rawValue = other.rawValue;
        rawUnit = other.rawUnit;
        source = other.source;
        direction = new String(other.direction);
    }

    /**
     * The timestamp of this reading, represented by the number of millseconds since the Unix Epoch
     */
    public long timestamp;
    /** The raw value reported, not normalized to a certain unit */
    public double rawValue;
    /** The Unit of the raw value */
    public Unit rawUnit;
    /** The kind of Source this reading is originating from */
    public Source source;
    // not sure about this one but it was in the original source, so it might prove usefull
    public String direction; // ??

    @NonNull
    @Override
    public String toString() {
        Date d = new Date(this.timestamp);

        return String.format("%s %s %s %s", d, this.source, this.rawValue, this.rawUnit);
    }
}

