package info.nightscout.androidaps.diabeatit.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import info.nightscout.androidaps.diabeatit.ui.log.event.BolusEvent;

@Dao
public interface BolusEventDao {
    @Query("SELECT * FROM BolusEvent")
    List<BolusEvent> getAll();

    @Query("SELECT * FROM BolusEvent ORDER BY timestamp DESC LIMIT 512")
    List<BolusEvent> getLimited();

    @Insert
    void insertAll(BolusEvent... events);

    @Delete
    void delete(BolusEvent event);
}
