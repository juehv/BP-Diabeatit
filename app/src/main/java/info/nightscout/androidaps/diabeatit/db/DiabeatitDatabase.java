package info.nightscout.androidaps.diabeatit.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import info.nightscout.androidaps.diabeatit.assistant.alert.Alert;
import info.nightscout.androidaps.diabeatit.log.event.BolusEvent;
import info.nightscout.androidaps.diabeatit.log.event.CarbsEvent;
import info.nightscout.androidaps.diabeatit.log.event.NoteEvent;
import info.nightscout.androidaps.diabeatit.log.event.SportsEvent;

/** Application database for the objects managed by the DiaBEATit project. */
@Database(entities = {
            Alert.class,
            BolusEvent.class,
            CarbsEvent.class,
            SportsEvent.class,
            NoteEvent.class
        }, version = 1, exportSchema = false)
@TypeConverters({info.nightscout.androidaps.diabeatit.db.TypeConverters.class})
public abstract class DiabeatitDatabase extends RoomDatabase {

    /** Get a data access object for {@link Alert}s */
    public abstract AlertDao alertDao();
    /** Get a data access object for {@link BolusEvent} */
    public abstract BolusEventDao bolusEventDao();
    /** Get a data access object for {@link CarbsEvent} */
    public abstract CarbsEventDao carbsEventDao();
    /** Get a data access object for {@link SportsEvent} */
    public abstract SportsEventDao sportsEventDao();
    /** Get a data access object for {@link NoteEvent} */
    public abstract NoteEventDao noteEventDao();

}
