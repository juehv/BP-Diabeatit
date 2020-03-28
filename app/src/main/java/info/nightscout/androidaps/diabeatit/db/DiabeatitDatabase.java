package info.nightscout.androidaps.diabeatit.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import info.nightscout.androidaps.diabeatit.assistant.alert.Alert;
import info.nightscout.androidaps.diabeatit.ui.log.event.BolusEvent;
import info.nightscout.androidaps.diabeatit.ui.log.event.CarbsEvent;
import info.nightscout.androidaps.diabeatit.ui.log.event.NoteEvent;
import info.nightscout.androidaps.diabeatit.ui.log.event.SportsEvent;

@Database(entities = {Alert.class, BolusEvent.class, CarbsEvent.class, SportsEvent.class, NoteEvent.class}, version = 1)
@TypeConverters({info.nightscout.androidaps.diabeatit.db.TypeConverters.class})
public abstract class DiabeatitDatabase extends RoomDatabase {
    // public static final String DEFAULT_DATABASE_NAME = "diabeatit";

    public abstract AlertDao alertDao();
    public abstract BolusEventDao bolusEventDao();
    public abstract CarbsEventDao carbsEventDao();
    public abstract SportsEventDao sportsEventDao();
    public abstract NoteEventDao noteEventDao();

}
