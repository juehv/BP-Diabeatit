package info.nightscout.androidaps.diabeatit.db;

import androidx.room.Database;

import info.nightscout.androidaps.diabeatit.assistant.alert.Alert;
import info.nightscout.androidaps.diabeatit.ui.log.event.BolusEvent;
import info.nightscout.androidaps.diabeatit.ui.log.event.CarbsEvent;
import info.nightscout.androidaps.diabeatit.ui.log.event.SportsEvent;

@Database(entities = {Alert.class}, version = 1)
public abstract class DiabeatitDatabase {
    public abstract AlertDao alertDao();
    public abstract BolusEventDao bolusEventDao();
    public abstract CarbsEventDao carbsEventDao();
    public abstract SportsEventDao sportsEventDao();

}
